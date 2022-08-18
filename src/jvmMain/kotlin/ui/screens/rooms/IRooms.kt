package ui.screens.rooms

import com.arkivanov.decompose.value.Value
import domain.Room

interface IRooms {

    val state: Value<State>

    data class State(
        val rooms: List<Room> = listOf()
    )
}