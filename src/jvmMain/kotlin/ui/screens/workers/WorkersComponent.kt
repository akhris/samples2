package ui.screens.workers

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import ui.screens.rooms.IRooms
import test.Rooms
import test.Workers

class WorkersComponent(
    componentContext: ComponentContext
) : IWorkers, ComponentContext by componentContext {

    private val _state = MutableValue(IWorkers.State(Workers.list))
    override val state: Value<IWorkers.State> = _state

}