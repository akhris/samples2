package ui.dialogs.import_from_file.import_measurements

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.rememberDialogState
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import persistence.export_import.json.dto.JSONMeasurement
import ui.dialogs.BaseDialog
import ui.dialogs.edit_sample_type_dialog.EditSampleTypeDialogUi
import ui.theme.DialogSettings
import ui.utils.sampletypes_selector.SampleTypesSelectorUi
import utils.DateTimeConverter
import utils.toFormattedList
import java.time.LocalDateTime
import kotlin.io.path.Path

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ImportMeasurementsUi(component: IImportMeasurements, onDismiss: () -> Unit) {

    val state by remember(component) { component.state }.subscribeAsState()

    val dialogState = rememberDialogState(
        size = DpSize(
            width = DialogSettings.defaultWideDialogWidth * 2,
            height = DialogSettings.defaultWideDialogHeight
        )
    )

    BaseDialog(
        dialogState = dialogState,
        onDismiss = onDismiss,
        title = {
            ListItem(text = { Text("Импорт данных измерений") }, secondaryText = {
                Text(Path(state.filePath).fileName.toString())
            }, modifier = Modifier.padding(16.dp))
        },
        content = {
            Row {
                Box(modifier = Modifier.weight(1f)) {
                    ImportMeasurementsSettingsContent(
                        component = component
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    ImportMeasurementsListContent(
                        component = component
                    )
                }
            }
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
private fun BoxScope.ImportMeasurementsSettingsContent(
    component: IImportMeasurements
) {
    val state by remember(component) { component.state }.subscribeAsState()

    val samplesToAdd = remember(state) { state.samplesToAdd }
    val parametersToAdd = remember(state) { state.parametersToAdd }
    val workersToAdd = remember(state) { state.workersToAdd }
    val placesToAdd = remember(state) { state.placesToAdd }
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

        if (samplesToAdd.isNotEmpty()) {
            ListItem(icon = {
                Icon(Icons.Rounded.Warning, contentDescription = "samples to be added")
            }, text = {
                Text(text = samplesToAdd.toFormattedList())
            },
                overlineText = {
                    Text(text = "Образцы, которые будут добавлены")
                })
        }

        if (parametersToAdd.isNotEmpty()) {
            ListItem(icon = {
                Icon(Icons.Rounded.Warning, contentDescription = "parameters to be added")
            }, text = {
                Text(text = parametersToAdd.toFormattedList())
            },
                overlineText = {
                    Text(text = "Параметры, которые будут добавлены")
                })
        }

        if (workersToAdd.isNotEmpty()) {
            ListItem(icon = {
                Icon(Icons.Rounded.Warning, contentDescription = "workers to be added")
            }, text = {
                Text(text = workersToAdd.toFormattedList())
            },
                overlineText = {
                    Text(text = "Сотрудники, которые будут добавлены")
                })
        }


        if (placesToAdd.isNotEmpty()) {
            ListItem(icon = {
                Icon(Icons.Rounded.Warning, contentDescription = "places to be added")
            }, text = {
                Text(text = placesToAdd.toFormattedList())
            },
                overlineText = {
                    Text(text = "Помещения, которые будут добавлены")
                })
        }


    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BoxScope.ImportMeasurementsListContent(component: IImportMeasurements) {
    val state by remember(component) { component.state }.subscribeAsState()
    val measurements = remember(state) { state.JSONMeasurements }
    LazyColumn(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        stickyHeader {
            Surface(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = "Данные для импорта (${measurements.size})"
                )
            }
        }

        itemsIndexed(measurements, key = { index, item -> index }) { index, item ->
            Card {
                renderJSONMEasurement(item)
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun renderJSONMEasurement(measurement: JSONMeasurement) {
    Column(modifier = Modifier.padding(8.dp).wrapContentSize()) {
        Row(verticalAlignment = Alignment.Bottom) {
            ListItem(
                modifier = Modifier.weight(1f),
                text = { Text(measurement.sample ?: "нет ID") },
                overlineText = { Text("ID образца") })
            Row(modifier = Modifier.weight(2f)) {
                val dateTime = remember(measurement) {
                    measurement.dateTime?.let {
                        //parse:
                        LocalDateTime.parse(it)
                    }?.let {
                        //format:
                        DateTimeConverter.dateTimeToString(it)
                    }
                }
                ListItem(
                    modifier = Modifier.weight(1f),
                    text = { Text(dateTime ?: "") },
                    overlineText = { Text("дата и время") })

                Checkbox(checked = true, onCheckedChange = {})
            }
        }

        Row(verticalAlignment = Alignment.Bottom) {
            ListItem(
                modifier = Modifier.weight(1f),
                text = { Text(measurement.comment ?: "") },
                overlineText = { Text("комментарий") })
            ListItem(
                modifier = Modifier.weight(1f),
                text = { Text(measurement.conditions ?: "") },
                overlineText = { Text("условия измерений") })

        }
        Row(verticalAlignment = Alignment.Bottom) {
            ListItem(
                modifier = Modifier.weight(1f),
                text = { Text(measurement.operator ?: "") },
                overlineText = { Text("оператор") })

            ListItem(
                modifier = Modifier.weight(1f),
                text = { Text(measurement.place ?: "") },
                overlineText = { Text("помещение") })

        }

        val results =
            remember(measurement) { measurement.results.map { "${it.parameter} = ${it.value}" }.toFormattedList() }
        if (results.isNotEmpty()) {
            ListItem(text = { Text(results) }, overlineText = { Text("результаты") })
        }
    }
}