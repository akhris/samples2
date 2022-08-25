package ui.screens.parameters

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.reduce
import com.arkivanov.essenty.lifecycle.subscribe
import domain.*
import domain.application.Result
import domain.application.baseUseCases.GetEntities
import domain.application.baseUseCases.InsertEntity
import domain.application.baseUseCases.RemoveEntity
import domain.application.baseUseCases.UpdateEntity
import kotlinx.coroutines.*
import org.kodein.di.DI
import org.kodein.di.instance
import test.Norms
import test.Parameters
import utils.log

class ParametersComponent(
    di: DI,
    componentContext: ComponentContext
) : IParameters, ComponentContext by componentContext {

    private val getParameters: GetEntities<Parameter> by di.instance()
    private val removeParameter: RemoveEntity<Parameter> by di.instance()
    private val insertParameter: InsertEntity<Parameter> by di.instance()
    private val updateParameter: UpdateEntity<Parameter> by di.instance()
    private val repoCallbacks: IRepositoryCallback<Parameter> by di.instance()

    private val scope =
        CoroutineScope(Dispatchers.Default + SupervisorJob())


    private val _state = MutableValue(IParameters.State(listOf()))
    override val state: Value<IParameters.State> = _state

    override fun addNewParameter(parameter: Parameter) {
        scope.launch {
            insertParameter(InsertEntity.Insert(parameter))
        }
    }

    override fun removeParameter(parameter: Parameter) {
        scope.launch {
            removeParameter(RemoveEntity.Remove(parameter))
        }
    }

    override fun updateParameter(parameter: Parameter) {
        scope.launch {
            updateParameter(UpdateEntity.Update(parameter))
        }
    }

    private suspend fun invalidateParameters() {
        val allParams = getParameters(GetEntities.Params.GetWithSpecification(Specification.QueryAll))
        when (allParams) {
            is Result.Failure -> {}
            is Result.Success -> {
                when (allParams.value) {
                    is EntitiesList.Grouped -> {}
                    is EntitiesList.NotGrouped -> {
                        _state.reduce {
                            it.copy(parameters = allParams.value.items)
                        }
                    }
                }
            }
        }
    }

    init {
        lifecycle.subscribe(onDestroy = {
            scope.coroutineContext.cancelChildren()
        })

        scope.launch {
            invalidateParameters()
        }

        scope.launch {
            repoCallbacks
                .updates
                .collect {
                    when (it) {
                        is RepoResult.ItemInserted,
                        is RepoResult.ItemRemoved,
                        is RepoResult.ItemUpdated -> {
                            invalidateParameters()
                        }
                    }
                }
        }
    }


}