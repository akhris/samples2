package ui.dialogs.file_picker_dialog

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import org.kodein.di.DI
import java.io.File
import javax.swing.filechooser.FileNameExtensionFilter

class FilePickerComponent(
    private val di: DI,
    componentContext: ComponentContext,
    private val onFileSelectedCallback: (file: File) -> Unit,
    title: String,
    fileFilters: List<FileNameExtensionFilter> = listOf(),
    pickerType: IFilePicker.PickerType = IFilePicker.PickerType.OpenFile
) : IFilePicker, ComponentContext by componentContext {

    private val _state =
        MutableValue(IFilePicker.State(title = title, fileFilters = fileFilters, pickerType = pickerType))

    override val state: Value<IFilePicker.State> = _state


    override fun onFileSelected(file: File) {
        onFileSelectedCallback(file)
    }

}