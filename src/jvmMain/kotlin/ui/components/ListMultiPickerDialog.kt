package ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.rememberDialogState
import ui.UiSettings
import ui.theme.DialogSettings
import utils.log

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
            Surface {
                Box(modifier = Modifier.fillMaxSize()) {
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
            }
        })
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
private fun <T> ItemsPickerDialogContent(
    modifier: Modifier = Modifier,
    items: List<T>,
    selectedItems: SnapshotStateList<T>,
    mapper: ((T) -> String)? = null,
    onSelectionChanged: (List<T>) -> Unit,
    isInverted: Boolean
) {

    val state = rememberLazyListState()

    val headerElevation by animateDpAsState(if (state.firstVisibleItemIndex == 0 && state.firstVisibleItemScrollOffset == 0) 0.dp else 4.dp)

    val selectedCount = remember(selectedItems.toList(), items, isInverted) {
        when (isInverted) {
            false -> selectedItems.size
            true -> items.size - selectedItems.size
        }
    }

    LazyColumn(
        state = state,
        modifier = modifier.fillMaxWidth()
    ) {
        if (items.size > 1) {
            stickyHeader {
                Surface(elevation = headerElevation) {
                    Box {
                        ListItem(
                            modifier = Modifier.fillMaxWidth(),
                            text = {
                                Text(
                                    "Выбрано: $selectedCount",
                                    style = LocalTextStyle.current.copy(fontWeight = FontWeight.Bold)
                                )
                            }, icon = {
                                TriStateCheckbox(
                                    state = when (isInverted) {
                                        true -> when (selectedItems.size) {
                                            items.size -> ToggleableState.Off
                                            0 -> ToggleableState.On
                                            else -> ToggleableState.Indeterminate
                                        }

                                        false -> when (selectedItems.size) {
                                            items.size -> ToggleableState.On
                                            0 -> ToggleableState.Off
                                            else -> ToggleableState.Indeterminate
                                        }
                                    },
                                    onClick = {
                                        when (isInverted) {
                                            true -> onSelectionChanged(
                                                when (selectedItems.size) {
                                                    0 -> items
                                                    else -> listOf()
                                                }
                                            )

                                            false -> onSelectionChanged(
                                                when (selectedItems.size) {
                                                    0 -> listOf()
                                                    else -> items
                                                }
                                            )
                                        }
                                    }
                                )
                            })
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .height(1.dp)
                                .fillParentMaxWidth()
                                .background(color = UiSettings.DataTable.dividerColor())
                        )
                    }
                }
            }
        }

        itemsIndexed(items, key = { index, item -> index }) { index, item ->
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
private fun <T> SelectableItem(
    item: T,
    isSelected: Boolean,
    mapper: ((T) -> String)? = null,
    onSelectionChanged: (isSelected: Boolean) -> Unit
) {
    ListItem(
        modifier = Modifier.fillMaxWidth(),
        text = {
            Text((mapper?.invoke(item) ?: item.toString()).ifEmpty { "<пустая строка>" })
        }, icon = {
            Checkbox(checked = isSelected, onCheckedChange = { onSelectionChanged(it) })
        })
}