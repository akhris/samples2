package ui.utils.sampletypes_selector

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.reduce
import com.arkivanov.essenty.lifecycle.subscribe
import domain.*
import domain.application.Result
import domain.application.baseUseCases.GetEntities
import domain.application.baseUseCases.RemoveEntity
import kotlinx.coroutines.*
import org.kodein.di.DI
import org.kodein.di.instance
import settings.PreferencesManager
import utils.log

class SampleTypesSelectorComponent(
    private val di: DI,
    componentContext: ComponentContext
) : ISampleTypesSelector, ComponentContext by componentContext {

    private val scope =
        CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val preferencesManager: PreferencesManager by di.instance()

    private val getSampleTypes: GetEntities<SampleType> by di.instance()
    private val removeSampleType: RemoveEntity<SampleType> by di.instance()
    private val samplesCallback: IRepositoryCallback<SampleType> by di.instance()

    private val _state = MutableValue(ISampleTypesSelector.State())

    override val state: Value<ISampleTypesSelector.State> = _state

    override fun selectType(type: SampleType?) {
        _state.reduce {
            it.copy(selectedType = type)
        }
    }

    private suspend fun invalidateSampleTypes() {
        val typesResult = getSampleTypes(GetEntities.Params.GetWithSpecification(Specification.QueryAll))
        when (typesResult) {
            is Result.Failure -> {
                log("cannot load sample types: ${typesResult.throwable.localizedMessage}")
            }

            is Result.Success -> {
                val types = typesResult.value.flatten()
                _state.reduce {
                    it.copy(
                        types = types,
                        selectedType = if ((it.selectedType == null) or !types.contains(it.selectedType)) types.firstOrNull() else it.selectedType
                    )
                }
            }
        }
    }

    init {

        lifecycle.subscribe(onDestroy = {
            scope.coroutineContext.cancelChildren()
        })

//        scope.launch {
//            invalidateSampleTypes()
//        }

        scope.launch {
            samplesCallback
                .updates
                .collect { result: RepoResult<SampleType> ->
                    when (result) {
                        is RepoResult.ItemInserted -> {
                            invalidateSampleTypes()
                            _state.reduce {
                                it.copy(selectedType = it.selectedType)
                            }
                        }

                        is RepoResult.ItemRemoved,
                        is RepoResult.ItemUpdated -> {
                            invalidateSampleTypes()
                        }
                    }
                }
        }

        scope.launch {
            preferencesManager
                .databaseFile
                .collect {
                    //database file changed -> invalidate sample types
//                    delay(200)
                    invalidateSampleTypes()
                }
        }
    }

}