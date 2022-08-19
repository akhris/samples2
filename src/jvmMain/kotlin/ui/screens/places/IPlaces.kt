package ui.screens.places

import com.arkivanov.decompose.value.Value
import domain.Place

interface IPlaces {

    val state: Value<State>

    data class State(
        val places: List<Place> = listOf()
    )
}