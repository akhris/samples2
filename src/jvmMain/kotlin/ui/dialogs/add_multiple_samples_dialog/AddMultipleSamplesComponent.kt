package ui.dialogs.add_multiple_samples_dialog

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.reduce
import com.arkivanov.essenty.lifecycle.subscribe
import kotlinx.coroutines.*
import org.kodein.di.DI
import utils.SampleIDUtils

class AddMultipleSamplesComponent(
    di: DI,
    componentContext: ComponentContext
) : IAddMultipleSamplesComponent, ComponentContext by componentContext {

    private val scope =
        CoroutineScope(Dispatchers.Default + SupervisorJob())


    private val _state: MutableValue<IAddMultipleSamplesComponent.State> =
        MutableValue(IAddMultipleSamplesComponent.State())

    override val state: Value<IAddMultipleSamplesComponent.State> = _state


    override fun parseIDs(rawString: String) {
        val parsed = SampleIDUtils.parseSampleIDs(rawString)
        _state.reduce {
            it.copy(rawString = rawString, parsedIDs = parsed)
        }
    }

    init {
        componentContext
            .lifecycle
            .subscribe(onDestroy = {
                scope.coroutineContext.cancelChildren()
            })

    }

}