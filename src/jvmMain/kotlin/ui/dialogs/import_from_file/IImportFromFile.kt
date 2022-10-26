package ui.dialogs.import_from_file

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import domain.IEntity
import ui.dialogs.import_from_file.import_measurements.ImportMeasurementsComponent

interface IImportFromFile<T : IEntity> {

    val state: Value<State<T>>

    val stack: Value<ChildStack<*, ImportDialog>>

    data class State<T : IEntity>(
        val filePath: String = "",
        val importedEntities: List<T> = listOf()
    )

    sealed class ImportDialog {
        class ImportMeasurementsDialog(val component: ImportMeasurementsComponent) : ImportDialog()
        object None : ImportDialog()
    }

}