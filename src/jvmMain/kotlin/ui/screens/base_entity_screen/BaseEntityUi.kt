package ui.screens.base_entity_screen

import LocalSamplesType
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.rememberDialogState
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import domain.IEntity
import ui.UiSettings
import ui.components.tables.Cell
import ui.components.tables.DataTable
import ui.components.tables.SelectionMode
import ui.components.tables.getTableWidth
import ui.dialogs.DatePickerDialog
import ui.dialogs.TimePickerDialog
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjusters

/**
 * Base Entity Screen:
 * 1. contains only DataTable
 * 2. handles cells clicks/selection
 * 3. shows EntityPicker dialog
 */
@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun <T : IEntity> BaseEntityUi(
    modifier: Modifier = Modifier,
    component: IEntityComponent<T>,
    selectionMode: SelectionMode<T> = SelectionMode.Multiple()
) {
    val state by component.state.subscribeAsState()

    //render samples list:
    val entities = remember(state) { state.entities }


    var dateTimePickerParams by remember { mutableStateOf<DateTimePickerDialogParams?>(null) }


//    Box(modifier = Modifier.fillMaxSize()) {

    //parameters table:
    DataTable(
        modifier = modifier,
//            modifier = Modifier.align(Alignment.TopCenter).padding(end = 48.dp),
        items = entities,
        mapper = component.dataMapper,
        onItemChanged = {
            component.updateEntity(it)
        },
        onCellClicked = { item, cell, column ->
            when (cell) {
                is Cell.EntityCell -> {
                    component.showEntityPickerDialog(
                        entity = cell.entity,
                        entityClass = cell.entityClass,
                        onSelectionChanged = {
                            val updatedItem = component.dataMapper.updateItem(
                                item = item,
                                columnId = column,
                                cell = cell.copy(entity = it)
                            )
                            component.updateEntity(updatedItem)
                        },
                        columnName = column.title
                    )
                }

                is Cell.DateTimeCell -> {
                    dateTimePickerParams =
                        DateTimePickerDialogParams(initialDateTime = cell.value,
                            onDateChanged = {
                                val updatedItem =
                                    component.dataMapper.updateItem(
                                        item,
                                        columnId = column,
                                        cell = cell.copy(value = it)
                                    )
                                component.updateEntity(updatedItem)
                            })
                }
            }
        },
        selectionMode = selectionMode
//            onSelectionChanged = onSelectionChanged
    )
//    }


    dateTimePickerParams?.let { params ->

        var dateTime by remember { mutableStateOf(params.initialDateTime) }

        var datePickerDialogShow by remember { mutableStateOf(true) }
        var timePickerDialogShow by remember { mutableStateOf(false) }

        if (datePickerDialogShow) {
            DatePickerDialog(
                initialSelection = dateTime?.toLocalDate(),
                onDismiss = { dateTimePickerParams = null },
                onDateSelected = { newDate ->
                    dateTime =
                        (dateTime ?: LocalDateTime.now())
                            .with(TemporalAdjusters.ofDateAdjuster {
                                newDate
                            }
                            )

                    timePickerDialogShow = true
                })

        }
        if (timePickerDialogShow) {
            TimePickerDialog(initialTime = dateTime, onDismiss = { dateTimePickerParams = null }, onTimeSelected = {
                params.onDateChanged(it)
            })
        }

    }



    Children(stack = component.dialogStack) {
        when (val child = it.instance) {
            is IEntityComponent.Dialog.EntityPicker<*> -> {
                val childTableWidth = remember { child.component.dataMapper.getTableWidth() + 128.dp }
                val dialogState = rememberDialogState(
                    width = childTableWidth,
                    height = UiSettings.Dialogs.defaultWideDialogHeight
                )

                var selection by remember { mutableStateOf<IEntity?>(null) }

                val sampleType = LocalSamplesType.current

                Dialog(
                    state = dialogState,
                    title = "Выбрать: ${child.columnName.lowercase()}",
                    onCloseRequest = {
                        component.dismissDialog()
                    }) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Box(modifier = Modifier.weight(1f)) {
                            BaseEntityUi(
                                component = child.component,
                                selectionMode = SelectionMode.Single(child.initialSelection, onItemSelected = {
                                    selection = it
                                })
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
                        ) {
                            TextButton(onClick = { component.dismissDialog() }) {
                                Text("Отмена")
                            }
                            TextButton(onClick = {
                                sampleType?.let {
                                    child.component.insertNewEntity(it)
                                }
                            }) {
                                Text("Добавить")
                            }
                            Button(onClick = {
//                                component.updateEntity()
                                child.onSelectionChanged(selection)
                                component.dismissDialog()
                            }) {
                                Text("Выбрать")
                            }
                        }
                    }
                }
            }

            IEntityComponent.Dialog.None -> {
                //render nothing
            }
        }
    }

}

data class DateTimePickerDialogParams(val initialDateTime: LocalDateTime?, val onDateChanged: (LocalDateTime?) -> Unit)