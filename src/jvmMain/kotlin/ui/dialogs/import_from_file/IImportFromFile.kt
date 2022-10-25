package ui.dialogs.import_from_file

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import domain.IEntity
import domain.Measurement
import ui.dialogs.import_from_file.import_measurements.IImportMeasurements
import ui.screens.base_entity_screen.IEntityComponent

interface IImportFromFile<T : IEntity> {

    val state: Value<State<T>>

    data class State<T : IEntity>(
        val filePath: String = "",
        val importedEntities: List<T> = listOf()
    )

}