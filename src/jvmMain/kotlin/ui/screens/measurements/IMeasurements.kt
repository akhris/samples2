package ui.screens.measurements

import com.arkivanov.decompose.value.Value
import domain.EntitiesList
import domain.Measurement
import ui.components.tables.IDataTableMapper
import ui.screens.base_entity_screen.IEntityComponent

interface IMeasurements {

    val state: Value<State>

    val dataMapper: Value<IDataTableMapper<Measurement>>

    data class State(
        val measurements: EntitiesList<Measurement> = EntitiesList.empty()
    )
}