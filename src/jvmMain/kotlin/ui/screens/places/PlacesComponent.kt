package ui.screens.places

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import test.Rooms

class PlacesComponent(
    componentContext: ComponentContext
) : IPlaces, ComponentContext by componentContext {

    private val _state = MutableValue(IPlaces.State(Rooms.list))
    override val state: Value<IPlaces.State> = _state

}