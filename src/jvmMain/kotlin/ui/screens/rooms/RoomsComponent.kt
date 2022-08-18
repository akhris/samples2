package ui.screens.rooms

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import test.Rooms

class RoomsComponent(
    componentContext: ComponentContext
) : IRooms, ComponentContext by componentContext {

    private val _state = MutableValue(IRooms.State(Rooms.list))
    override val state: Value<IRooms.State> = _state

}