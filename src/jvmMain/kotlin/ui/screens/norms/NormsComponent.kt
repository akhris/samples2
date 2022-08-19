package ui.screens.norms

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import test.Norms
import ui.screens.parameters.IParameters

class NormsComponent(
    componentContext: ComponentContext
) : INorms, ComponentContext by componentContext {

    private val _state = MutableValue(INorms.State(Norms.norms))
    override val state: Value<INorms.State> = _state

}