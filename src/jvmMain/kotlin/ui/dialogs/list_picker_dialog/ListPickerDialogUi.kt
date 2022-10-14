package ui.dialogs.list_picker_dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.rememberDialogState
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import ui.theme.DialogSettings

@Composable
fun ListPickerDialogUi(component: IListPickerDialogComponent, onDismiss: () -> Unit) {

    val state by remember(component) { component.state }.subscribeAsState()

    val dialogState = rememberDialogState(
        size = DpSize(
            width = DialogSettings.defaultWideDialogWidth,
            height = DialogSettings.defaultWideDialogHeight
        )
    )


    Dialog(
        state = dialogState,
        title = state.title,
        onCloseRequest = onDismiss,
        content = {
            ListPickerDialogContent(
                items = state.items,
                mode = state.mode,
                onDismiss = onDismiss
            )
        })

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ListPickerDialogContent(items: List<ListPickerItem>, mode: ListPickerMode, onDismiss: () -> Unit) {

    val selection = remember(mode) {
        when (mode) {
            is ListPickerMode.SingleSelect -> listOfNotNull(mode.initialSelection).toMutableStateList()
        }
    }

    Column {
        items.forEach { item ->
            ListItem(
                icon = {
                    when (mode) {
                        is ListPickerMode.SingleSelect -> {
                            RadioButton(selected = item.id in selection, onClick = {
                                (if (item.id !in selection) {
                                    selection.clear()
                                    selection.add(item.id)
                                })
                            })
                        }
                    }
                },
                text = {
                    Text(item.title)
                },
                secondaryText = item.caption?.let { c ->
                    {
                        Text(c)
                    }
                }
            )
        }
        //buttons row:
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = {
                onDismiss()
            }) {
                Text("Отмена")
            }
            Button(
                enabled = selection.isNotEmpty(),
                onClick = {
                    when (mode) {
                        is ListPickerMode.SingleSelect -> {
                            selection.firstOrNull()?.let {
                                onDismiss()
                                mode.onItemSelected?.invoke(it)
                            }
                        }
                    }
                }) {
                Text("Выбрать")
            }
        }
    }
}