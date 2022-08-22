package ui.screens.samples

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import test.Norms
import test.Samples

class SamplesComponent(
    componentContext: ComponentContext
) : ISamples, ComponentContext by componentContext {

    private val _state = MutableValue(ISamples.State(Samples.samples))
    override val state: Value<ISamples.State> = _state

}