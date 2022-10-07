package ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.rememberDialogState
import ui.theme.DialogSettings

@Composable
fun <T> ListPickerDialog(
    items: List<T>,
    title: String,
    mapper: @Composable BoxScope.(T) -> Unit,
    initSelection: T? = null,
    autoPick: Boolean = false,
    onItemPicked: (T) -> Unit,
    onDismiss: () -> Unit
) {

    val state = rememberDialogState(
        size = DpSize(
            width = DialogSettings.defaultWideDialogWidth,
            height = DialogSettings.defaultWideDialogHeight
        )
    )


    Dialog(
        state = state,
        title = title,
        onCloseRequest = onDismiss,
        content = {
            PickerDialogContent(
                items = items,
                initSelection = initSelection,
                mapper = mapper,
                autoPick = autoPick
            ) {
                onItemPicked(it)
                onDismiss()
            }
        })
}

@Composable
private fun <T> PickerDialogContent(
    items: List<T>,
    initSelection: T?,
    mapper: @Composable (BoxScope.(T) -> Unit),
    autoPick: Boolean = false,
    onItemPicked: (T) -> Unit
) {
    var selectedEntity by remember(initSelection) { mutableStateOf<T?>(initSelection) }
    Surface {
        Column(modifier = Modifier.fillMaxHeight()) {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(items) {
                    Surface(
                        modifier = Modifier.clickable {
                            selectedEntity = it
                        },
                        color = when (it == selectedEntity) {
                            true -> MaterialTheme.colors.primary
                            false -> MaterialTheme.colors.surface
                        }
                    ) {
                        Box {
                            mapper(it)
                        }
                    }
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {

                Button(
                    modifier = Modifier.padding(8.dp),
                    onClick = {
                        selectedEntity?.let { onItemPicked(it) }
                    },
                    content = { Text("ok") }
                )
            }
        }
    }

    LaunchedEffect(autoPick, selectedEntity) {
        if (autoPick && (selectedEntity != initSelection)) {
            selectedEntity?.let { onItemPicked(it) }
        }
    }

}