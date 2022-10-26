package ui.dialogs.import_from_file.import_measurements

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog
import ui.dialogs.BaseDialog

@Composable
fun ImportMeasurementsUi(component: IImportMeasurements, onDismiss: () -> Unit) {

    BaseDialog(
        onDismiss = onDismiss,
        title = {
            Text(text = "Импорт данных измерений")
        },
        content = {
            Text("importing")
        },
        buttons = {
            Button(onClick = {}) {
                Text("Сохранить")
            }
        }
    )
}