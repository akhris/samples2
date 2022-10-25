package ui.dialogs.import_from_file

import androidx.compose.foundation.layout.Box
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import domain.IEntity
import ui.dialogs.BaseDialog
import ui.dialogs.import_from_file.import_measurements.ImportMeasurementsUi
import utils.log

@Composable
fun <T : IEntity> ImportFromFileUi(component: IImportFromFile<T>, onDismiss: () -> Unit) {
    BaseDialog(onDismiss = onDismiss, title = {
        Text("Импортировать объекты")
    }, content = {
        Text("ImportFromFileUi content")
    }, buttons = {
        TextButton(onClick = onDismiss) {
            Text("Отмена")
        }
    })

}