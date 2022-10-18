package ui.dialogs.file_picker_dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import utils.log
import java.io.File
import javax.swing.JFileChooser
import javax.swing.JOptionPane
import javax.swing.filechooser.FileNameExtensionFilter
import javax.swing.filechooser.FileSystemView


@Composable
fun FilePickerUi(component: IFilePicker, onDismiss: () -> Unit) {
    val state by remember(component) { component.state }.subscribeAsState()
    val file =
        remember(state.title, state.fileFilters) { fileChooserDialog(state.title, state.fileFilters, state.pickerType) }
    LaunchedEffect(file) {
        file?.let { component.onFileSelected(it) }
        onDismiss()
    }
}


//@Composable
//private fun FileDialog(
//    initDir: String? = null,
//    parent: Frame? = null,
//    onCloseRequest: (result: String?) -> Unit
//) = AwtWindow(
//    create = {
//        object : FileDialog(parent, "Choose a file", LOAD) {
//
//            override fun setVisible(value: Boolean) {
//                super.setVisible(value)
//                if (value) {
//                    if (directory != null && file != null)
//                        onCloseRequest(Path(directory, file).toString())
//                }
//            }
//
//        }
//    },
//    dispose = FileDialog::dispose,
//    update = { dialog ->
//        dialog.filenameFilter = FilenameFilter { dir, name ->
//            Path(name).extension == "db"
//        }
//
//        initDir?.let {
//            dialog.directory = Path(it).parent.toString()
//        }
//
//    }
//)

fun fileChooserDialog(
    title: String?,
    filters: List<FileNameExtensionFilter> = listOf(),
    pickerType: IFilePicker.PickerType
): File? {
    val fileChooser = JFileChooser(FileSystemView.getFileSystemView())
    fileChooser.currentDirectory = File(System.getProperty("user.dir"))
    fileChooser.dialogTitle = title
    fileChooser.fileSelectionMode = JFileChooser.FILES_ONLY
    fileChooser.isFileHidingEnabled = false
    filters.forEach { fileChooser.addChoosableFileFilter(it) }
    fileChooser.isAcceptAllFileFilterUsed = false
    fileChooser.selectedFile = null
    fileChooser.currentDirectory = null

    val returnValue = when (pickerType) {
        IFilePicker.PickerType.OpenFile -> fileChooser.showOpenDialog(null)
        IFilePicker.PickerType.SaveFile -> fileChooser.showSaveDialog(null)
    }

    val file = if (returnValue == JFileChooser.APPROVE_OPTION) {
        fileChooser.selectedFile?.let {
            //check extension:
            //if empty - try to add extension from selected extension filter
            val nameFilter = (fileChooser.fileFilter as? FileNameExtensionFilter) ?: return it
            val extension = it.extension
            if (extension !in nameFilter.extensions) {
                File(it.toString() + (nameFilter.extensions.firstOrNull()?.let { ext -> ".$ext" } ?: ""))
            } else {
                it
            }
        }
    } else {
        null
    }



    log("got file: $file with fileFilter: ${fileChooser.fileFilter.description}")


    return file

}