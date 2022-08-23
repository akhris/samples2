package ui.screens.samples

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.reduce
import com.arkivanov.essenty.lifecycle.subscribe
import domain.EntitiesList
import domain.Sample
import domain.Specification
import domain.application.Result
import domain.application.baseUseCases.GetEntities
import kotlinx.coroutines.*
import org.kodein.di.DI
import org.kodein.di.instance
import test.Samples
import utils.log

class SamplesComponent(
    di: DI,
    componentContext: ComponentContext
) : ISamples, ComponentContext by componentContext {

    private val getSamples: GetEntities<Sample> by di.instance()

    private val scope =
        CoroutineScope(Dispatchers.Default + SupervisorJob())


    private val _state = MutableValue(ISamples.State())
    override val state: Value<ISamples.State> = _state

    private suspend fun invalidateSamples() {
        //get all samples
        val samples = getSamples(GetEntities.Params.GetWithSpecification(Specification.QueryAll))

        when (samples) {
            is Result.Success -> {
                _state.reduce {
                    it.copy(
                        samples = when (val list = samples.value) {
                            is EntitiesList.Grouped -> listOf()
                            is EntitiesList.NotGrouped -> list.items
                        }
                    )
                }
            }

            is Result.Failure -> {
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

    }
}