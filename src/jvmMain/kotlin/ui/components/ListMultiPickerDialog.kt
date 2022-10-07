package ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.rememberDialogState
import ui.theme.DialogSettings

@Composable
fun <T> ListMultiPickerDialog(
    items: List<T>,
    title: String,
    initialSelection: List<T> = listOf(),
    mapper: ((T) -> String)? = null,
    onItemsPicked: (List<T>) -> Unit,
    onDismiss: () -> Unit,
    isInverted: Boolean = false
) {

    val selectedItems = remember(initialSelection) { initialSelection.toMutableStateList() }

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

            Box {
                var buttonsHeight by remember { mutableStateOf(0.dp) }

                ItemsPickerDialogContent(
                    modifier = Modifier.padding(bottom = buttonsHeight),
                    items = items,
                    selectedItems = selectedItems,
                    onSelectionChanged = {
                        selectedItems.clear()
                        selectedItems.addAll(it)
                    },
                    mapper = mapper,
                    isInverted = isInverted
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onSizeChanged { buttonsHeight = it.height.dp }
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(space = 8.dp, alignment = Alignment.End)
                ) {
                    TextButton(onClick = { onDismiss() }) {
                        Text("Отмена")
                    }
                    Button(onClick = { onItemsPicked(selectedItems) }) {
                        Text("Выбрать")
                    }
                }
            }
        })
}

@Composable
private fun <T> ItemsPickerDialogContent(
    modifier: Modifier = Modifier,
    items: List<T>,
    selectedItems: List<T>,
    mapper: ((T) -> String)? = null,
    onSelectionChanged: (List<T>) -> Unit,
    isInverted: Boolean
) {

    Column(
        modifier = modifier.fillMaxWidth().verticalScroll(rememberScrollState())
    ) {
        items.forEach { item ->
            SelectableItem(
                item,
                isSelected = if (!isInverted) selectedItems.contains(item) else !selectedItems.contains(item),
                onSelectionChanged = {
                    if (it) {
                        onSelectionChanged(if (!isInverted) selectedItems.plus(item) else selectedItems.minus(item))
                    } else {
                        onSelectionChanged(if (!isInverted) selectedItems.minus(item) else selectedItems.plus(item))
                    }
                },
                mapper = mapper
            )
        }
    }


}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun <T> ColumnScope.SelectableItem(
    item: T,
    isSelected: Boolean,
    mapper: ((T) -> String)? = null,
    onSelectionChanged: (isSelected: Boolean) -> Unit
) {

    ListItem(
        modifier = Modifier.fillMaxWidth(),
        text = {
            Text(mapper?.invoke(item) ?: item.toString())
        }, icon = {
            Checkbox(checked = isSelected, onCheckedChange = { onSelectionChanged(it) })
        })
}