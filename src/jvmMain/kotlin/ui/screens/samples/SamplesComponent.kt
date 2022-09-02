package ui.screens.samples

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.reduce
import com.arkivanov.essenty.lifecycle.subscribe
import domain.*
import domain.application.Result.Failure
import domain.application.Result.Success
import domain.application.baseUseCases.GetEntities
import domain.application.baseUseCases.InsertEntity
import domain.application.baseUseCases.UpdateEntity
import kotlinx.coroutines.*
import org.kodein.di.DI
import org.kodein.di.instance
import utils.log


class SamplesComponent(
    di: DI,
    componentContext: ComponentContext
) : ISamples, ComponentContext by componentContext {

    private val getSamples: GetEntities<Sample> by di.instance()
    private val insertSample: InsertEntity<Sample> by di.instance()
    private val updateSample: UpdateEntity<Sample> by di.instance()

    private val repositoryCallbacks: IRepositoryCallback<Sample> by di.instance()

    private val scope =
        CoroutineScope(Dispatchers.Default + SupervisorJob())


    private val _state = MutableValue(ISamples.State())
    override val state: Value<ISamples.State> = _state

    override fun insertNewSample(sample: Sample) {
        scope.launch {
            insertSample(InsertEntity.Insert(sample))
        }
    }

    override fun updateSample(sample: Sample) {
        scope.launch {
            val result = updateSample(UpdateEntity.Update(sample))
            log("updating sample: $result")
        }
    }

    private suspend fun invalidateSamples() {
        //get all samples
        val samples = getSamples(GetEntities.Params.GetWithSpecification(Specification.QueryAll))

        when (samples) {
            is Success -> {
                _state.reduce {
                    it.copy(
                        samples = when (val list = samples.value) {
                            is EntitiesList.Grouped -> listOf()
                            is EntitiesList.NotGrouped -> list.items
                        }
                    )
                }
            }

            is Failure -> {
                log(samples.throwable)
                log(samples.throwable.stackTraceToString())
            }

        }
    }

    init {
        lifecycle.subscribe(onDestroy = {
            scope.coroutineContext.cancelChildren()
        })

        scope.launch {
            invalidateSamples()
        }

        //subscribe to repository callbacks:
        scope.launch {
            repositoryCallbacks.updates.collect {
                when (it) {
                    is RepoResult.ItemInserted,
                    is RepoResult.ItemRemoved,
                    is RepoResult.ItemUpdated -> {
                        invalidateSamples()
                    }
                }
            }
        }

    }
}