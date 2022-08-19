package ui.screens.operationtypes

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import test.OperationTypes

class OperationTypesComponent(
    componentContext: ComponentContext
) : IOperationTypes, ComponentContext by componentContext {

    private val _state = MutableValue(IOperationTypes.State(OperationTypes.list))
    override val state: Value<IOperationTypes.State> = _state

}