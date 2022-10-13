package ui.dialogs.file_picker_dialog

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.toArgb
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import utils.log
import java.awt.Color
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter
import javax.swing.filechooser.FileSystemView


@Composable
fun FilePickerUi(component: IFilePicker, onDismiss: () -> Unit) {
    val state by remember(component) { component.state }.subscribeAsState()
    val file = remember(state.title, state.fileFilters) { fileChooserDialog(state.title, state.fileFilters) }
    LaunchedEffect(file) {
        if (file.isNotEmpty())
            component.onFileSelected(file)
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
    filters: List<FileNameExtensionFilter> = listOf()
): String {
    val fileChooser = JFileChooser(FileSystemView.getFileSystemView())
    fileChooser.currentDirectory = File(System.getProperty("user.dir"))
    fileChooser.dialogTitle = title
    fileChooser.fileSelectionMode = JFileChooser.FILES_AND_DIRECTORIES
    filters.forEach { fileChooser.addChoosableFileFilter(it) }
    fileChooser.isAcceptAllFileFilterUsed = false
    fileChooser.selectedFile = null
    fileChooser.currentDirectory = null

    val file = if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
        fileChooser.selectedFile.toString()
    } else {
        ""
    }
    return file

}