package ui.dialogs.import_from_file.import_measurements

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import domain.SampleType
import ui.components.ListSelector
import ui.dialogs.BaseDialog
import ui.dialogs.edit_sample_type_dialog.EditSampleTypeDialogUi
import ui.utils.sampletypes_selector.SampleTypesSelectorUi
import java.nio.file.Path
import kotlin.io.path.Path

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ImportMeasurementsUi(component: IImportMeasurements, onDismiss: () -> Unit) {

    val state by remember(component) { component.state }.subscribeAsState()


    BaseDialog(
        onDismiss = onDismiss,
        title = {
            ListItem(text = { Text("Импорт данных измерений") }, secondaryText = {
                Text(Path(state.filePath).fileName.toString())
            }, modifier = Modifier.padding(16.dp))
        },
        content = {
            ImportMeasurementsContent(
                component = component
            )
        },
        buttons = {
            Button(onClick = {}) {
                Text("Сохранить")
            }
        }
    )
}

@OptIn(ExperimentalDecomposeApi::class, ExperimentalMaterialApi::class)
@Composable
private fun BoxScope.ImportMeasurementsContent(
    component: IImportMeasurements
) {
    val state by remember(component) { component.state }.subscribeAsState()

    val parametersToAdd = remember(state) { state.parametersToAdd }
    val sampleTypesStack by remember(component) { component.sampleTypesStack }.subscribeAsState()

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Children(sampleTypesStack) {
            when (val child = it.instance) {
                is IImportMeasurements.SampleTypesUtils.SampleTypesSelector -> {
                    SampleTypesSelectorUi(component = child.component, onSampleTypeSelected = {
                        it?.let { component.selectSampleType(it) }
                    }, onEditSampleTypeClick = {
                        component.editSampleType(it)
                    })
                }

                is IImportMeasurements.SampleTypesUtils.EditSampleTypesDialog -> {
                    EditSampleTypeDialogUi(
                        component = child.component,
                        onDismiss = { component.dismissEditSampleType() })
                }
            }
        }
        val params = remember(parametersToAdd) {
            val builder = StringBuilder()
            parametersToAdd.forEachIndexed { index, s ->
                builder.append(s)
                if (index < parametersToAdd.size - 1) {
                    builder.append(", ")
                }
            }
            builder.toString()
        }
        if (params.isNotEmpty()) {
            ListItem(icon = {
                Icon(Icons.Rounded.Warning, contentDescription = "parameters to be added")
            }, text = {
                Text(text = params)
            },
                overlineText = {
                    Text(text = "Параметры, которые будут добавлены")
                })
        }
    }
}