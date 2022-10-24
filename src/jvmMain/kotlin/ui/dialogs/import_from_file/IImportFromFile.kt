package ui.dialogs.import_from_file

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import ui.dialogs.import_from_file.import_measurements.IImportMeasurements
import ui.screens.base_entity_screen.IEntityComponent

interface IImportFromFile {

    val dialogStack: Value<ChildStack<*, ImportEntity>>

    sealed class ImportEntity {
        object None : ImportEntity()
        data class Measurements(val component: IImportMeasurements) : ImportEntity()
    }
}