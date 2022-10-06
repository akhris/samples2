package ui.screens.sample_details_screen

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.reduce
import com.arkivanov.essenty.lifecycle.subscribe
import domain.*
import domain.application.Result
import domain.application.baseUseCases.GetEntities
import kotlinx.coroutines.*
import org.kodein.di.DI
import org.kodein.di.instance
import ui.components.tables.ColumnId

class SampleDetailsComponent(
    private val di: DI,
    componentContext: ComponentContext,
    private val sample: Sample
) : ISampleDetailsComponent, ComponentContext by componentContext {

    private val scope =
        CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val getOperations: GetEntities<Operation> by di.instance()

    private val _stateOperations = MutableValue<List<Operation>>(listOf())

    override val stateSample: Value<Sample> = MutableValue(sample)

    override val stateOperations: Value<List<Operation>> = _stateOperations

    // TODO: Add children components for operations with initial filter spec!

    private suspend fun invalidateOperations() {
        val operations = getOperations(
            GetEntities.Params.GetWithSpecification(
                Specification.Filtered(
                    listOf(FilterSpec.Values(listOf(sample.id), columnName = "sample"))
                )
            )
        )
        when (operations) {
            is Result.Failure -> {}
            is Result.Success -> {
                _stateOperations.reduce {
                    when (val ops = operations.value) {
                        is EntitiesList.Grouped -> listOf()
                        is EntitiesList.NotGrouped -> ops.items
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
            invalidateOperations()
        }
    }
}