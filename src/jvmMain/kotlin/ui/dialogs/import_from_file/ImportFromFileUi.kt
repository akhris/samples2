package ui.dialogs.import_from_file

import androidx.compose.foundation.layout.Box
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import domain.IEntity
import ui.dialogs.BaseDialog
import ui.dialogs.import_from_file.import_measurements.ImportMeasurementsUi
import utils.log

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun <T : IEntity> ImportFromFileUi(component: IImportFromFile<T>, onDismiss: () -> Unit) {
    val stack by remember(component) { component.stack }.subscribeAsState()

    Children(stack = stack) {
        when (val child = it.instance) {
            is IImportFromFile.ImportDialog.ImportMeasurementsDialog -> {
                ImportMeasurementsUi(component = child.component, onDismiss = onDismiss)
            }

            IImportFromFile.ImportDialog.None -> {
                //do nothing
            }
        }
    }
}
