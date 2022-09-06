package ui.screens.base_entity_screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
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

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun <T : IEntity> BaseEntityUi(
    component: IEntityComponent<T>,
    selectionMode: SelectionMode = SelectionMode.Multiple,
    onSelectionChanged: ((List<T>) -> Unit)? = null
) {
    val state by component.state.subscribeAsState()

    //render samples list:
    val entities = remember(state) { state.entities }


    Box(modifier = Modifier.fillMaxSize()) {

        //parameters table:
        DataTable(
            modifier = Modifier.align(Alignment.TopCenter).padding(end = 80.dp),
            items = entities,
            mapper = component.dataMapper,
            onItemChanged = {
                component.updateEntity(it)
            },
            onCellClicked = { item, cell, column ->
                if (cell is Cell.EntityCell) {
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
                        }
                    )
                }
            },
            selectionMode = selectionMode,
            onSelectionChanged = onSelectionChanged
        )


        FloatingActionButton(
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            onClick = {
                //todo show add sample dialog
            },
            content = { Icon(Icons.Rounded.Add, contentDescription = "add parameter") })
    }

    Children(stack = component.dialogStack) {
        when (val child = it.instance) {
            is IEntityComponent.Dialog.EntityPicker<*> -> {
                val dialogState = rememberDialogState(
                    width = UiSettings.Dialogs.defaultWideDialogWidth,
                    height = UiSettings.Dialogs.defaultWideDialogHeight
                )

                var selection by remember { mutableStateOf<IEntity?>(null) }

                Dialog(
                    state = dialogState,
                    onCloseRequest = {
                        component.dismissDialog()
                    }) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Box(modifier = Modifier.weight(1f)) {
                            BaseEntityUi(
                                component = child.component,
                                selectionMode = SelectionMode.Single(child.initialSelection),
                                onSelectionChanged = {
                                    selection = it.firstOrNull()
                                })
                        }
                        Row(modifier = Modifier.fillMaxWidth()) {
                            TextButton(onClick = { component.dismissDialog() }) {
                                Text("Отмена")
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