package ui.screens.measurements

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.reduce
import com.arkivanov.essenty.lifecycle.subscribe
import domain.*
import domain.application.Result
import domain.application.baseUseCases.GetEntities
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import org.kodein.di.DI
import org.kodein.di.instance
import ui.components.tables.IDataTableMapper
import utils.log

class MeasurementsComponent(
    private val di: DI,
    componentContext: ComponentContext
) : IMeasurements, ComponentContext by componentContext {

    private val getMeasurements: GetEntities<Measurement> by di.instance()
    private val getParameters: GetEntities<Parameter> by di.instance()
    private val measurementCallbacks: IRepositoryCallback<Measurement> by di.instance()
    private val parametersCallbacks: IRepositoryCallback<Parameter> by di.instance()

    private val scope =
        CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val _state = MutableValue<IMeasurements.State>(IMeasurements.State())

    override val state: Value<IMeasurements.State> = _state

    private val emptyMapper: IDataTableMapper<Measurement> by di.instance(arg = listOf<Parameter>())

    private val _dataMapper = MutableValue<IDataTableMapper<Measurement>>(emptyMapper)

    override val dataMapper: Value<IDataTableMapper<Measurement>> = _dataMapper

    private suspend fun invalidateDataMapper() {
        val parameters = getParameters(GetEntities.Params.GetWithSpecification(Specification.QueryAll))
        if (parameters is Result.Success) {
            (parameters.value as? EntitiesList.NotGrouped<Parameter>)?.let { params ->
                _dataMapper.reduce {
                    val a: IDataTableMapper<Measurement> by di.instance(arg = params.items)
                    log("got new mapper for params: ${params.items}:")
                    log(a.columns)
                    a
                }
            }
        }
    }

    private suspend fun invalidateMeasurements() {
        val measurements = getMeasurements(GetEntities.Params.GetWithSpecification(Specification.QueryAll))
        if (measurements is Result.Success) {
            _state.reduce {
                it.copy(measurements = measurements.value)
            }
        }
    }

    init {
        lifecycle.subscribe(onDestroy = {
            scope.coroutineContext.cancelChildren()
        })

        scope.launch {
            invalidateDataMapper()
            invalidateMeasurements()
        }

        scope.launch {
            parametersCallbacks
                .updates
                .collect {
                    invalidateDataMapper()
                }
        }

        scope.launch {
            measurementCallbacks
                .updates
                .collect {
                    invalidateMeasurements()
                }
        }


    }
}