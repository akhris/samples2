package ui.screens.measurements

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.subscribe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import org.kodein.di.DI

class MeasurementsComponent(
    private val di: DI,
    componentContext: ComponentContext
) : IMeasurements, ComponentContext by componentContext {
    private val scope =
        CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val _state = MutableValue<IMeasurements.State>(IMeasurements.State())

    override val state: Value<IMeasurements.State> = _state

    init {
        lifecycle.subscribe(onDestroy = {
            scope.coroutineContext.cancelChildren()
        })
    }
}