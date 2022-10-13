package ui.dialogs.file_picker_dialog

import com.arkivanov.decompose.value.Value
import javax.swing.filechooser.FileNameExtensionFilter

interface IFilePicker {

    val state: Value<State>

    fun onFileSelected(filePath: String)

    data class State(
        val title: String = "",
        val fileFilters: List<FileNameExtensionFilter> = listOf()
    )
}