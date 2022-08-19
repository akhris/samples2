package ui.screens.parameters

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import test.Norms
import test.Parameters

class ParametersComponent(
    componentContext: ComponentContext
) : IParameters, ComponentContext by componentContext {

    private val _state = MutableValue(IParameters.State(Parameters.list))
    override val state: Value<IParameters.State> = _state

}