package ui.screens.measurements

import com.arkivanov.decompose.value.Value
import domain.Measurement
import ui.screens.base_entity_screen.IEntityComponent

interface IMeasurements {

    val state: Value<State>

    data class State(
        val measurements: List<Measurement> = listOf()
    )
}