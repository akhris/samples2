package ui.dialogs.file_picker_dialog

import com.arkivanov.decompose.value.Value
import java.io.File
import javax.swing.filechooser.FileNameExtensionFilter

interface IFilePicker {

    val state: Value<State>

    fun onFileSelected(file: File)

    data class State(
        val title: String = "",
        val fileFilters: List<FileNameExtensionFilter> = listOf(),
        val pickerType: PickerType = PickerType.OpenFile
    )

    sealed class PickerType {
        object OpenFile : PickerType()
        object SaveFile : PickerType()
    }
}