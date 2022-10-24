package ui.dialogs.import_from_file

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import ui.dialogs.import_from_file.import_measurements.ImportMeasurementsUi

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun ImportFromFileUi(component: IImportFromFile, onDismiss: () -> Unit) {

    Children(component.dialogStack) {
        when (val dialog = it.instance) {
            is IImportFromFile.ImportEntity.Measurements -> {
                ImportMeasurementsUi(component = dialog.component, onDismiss = onDismiss)
            }
        }
    }

}