package ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ui.UiSettings

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun <T> ListSelector(
    modifier: Modifier = Modifier,
    currentSelection: T? = null,
    items: List<T> = listOf(),
    onAddNewClicked: () -> Unit,
    onItemSelected: (T?) -> Unit,
    onItemDelete: (T) -> Unit,
    itemName: (T) -> String,
    title: String? = null
) {
    var isMenuOpened by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(if (isMenuOpened) 180f else 0f)

//    var showNewSampleTypeDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf<T?>(null) }



    Box(modifier = modifier) {
        Row(modifier = Modifier.align(Alignment.Center), verticalAlignment = Alignment.CenterVertically) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = currentSelection?.let { itemName(it) } ?: "",
                textAlign = TextAlign.Center)
            if (items.isNotEmpty()) {
                IconButton(onClick = {
                    isMenuOpened = !isMenuOpened
                }) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowDropDown,
                        modifier = Modifier.size(UiSettings.SampleTypesSelector.dropDownIconSize)
                            .rotate(rotation),
                        contentDescription = "open drop-down",
                        tint = MaterialTheme.colors.onPrimary.copy(alpha = 0.75f)
                    )
                }
            } else {
                IconButton(onClick = onAddNewClicked) {
                    Icon(
                        Icons.Rounded.AddCircle,
                        modifier = Modifier.padding(8.dp),
                        contentDescription = "add new item",
                        tint = MaterialTheme.colors.onPrimary.copy(alpha = 0.75f)
                    )
                }
            }
        }

        DropdownMenu(
            modifier = modifier,
            expanded = isMenuOpened,
            onDismissRequest = {
                isMenuOpened = false
            }
        ) {
            items.forEach { item ->

                var isHover by remember { mutableStateOf(false) }

                DropdownMenuItem(
                    modifier = Modifier.width(320.dp),
                    onClick = {
                        onItemSelected(item)
                        isMenuOpened = false
                    }) {
                    Text(modifier = Modifier.weight(1f), text = itemName(item))

                    Icon(
                        modifier =
                        Modifier
                            .onPointerEvent(PointerEventType.Enter) { isHover = true }
                            .onPointerEvent(PointerEventType.Exit) { isHover = false }
                            .clickable {
                                showDeleteConfirmDialog = item
                            },
                        imageVector = Icons.Rounded.Clear,
                        contentDescription = "delete ${itemName(item)}",
                        tint = MaterialTheme.colors.error.copy(alpha = if (isHover) 1.0f else 0.2f)
                    )
                }
            }
            DropdownMenuItem(modifier = Modifier.width(320.dp), onClick = onAddNewClicked) {
                Icon(
                    Icons.Rounded.AddCircle,
                    modifier = Modifier.padding(end = 8.dp),
                    contentDescription = "add new item"
                )
                Text(modifier = Modifier.weight(1f), text = "добавить")
            }

        }
    }


    showDeleteConfirmDialog?.let { item ->
        AlertDialog(onDismissRequest = {
            showDeleteConfirmDialog = null
        }, confirmButton = {
            Button(
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.error),
                onClick = {
                    if (currentSelection == item) {
                        //change selection:
                        val removedIndex = items.indexOf(item)
                        onItemSelected(
                            if (items.size > 1) {
                                if (removedIndex == 0) {
                                    items[1]
                                } else {
                                    items.getOrNull(removedIndex - 1)
                                }
                            } else {
                                null
                            }
                        )
                    }
                    onItemDelete(item)
                    showDeleteConfirmDialog = null
                }) {
                Text(text = "Удалить", color = MaterialTheme.colors.onError)
            }
        }, text = {
            Text(text = "Удалить ${itemName(item)}?")
        }, dismissButton = {
            OutlinedButton(onClick = {
                showDeleteConfirmDialog = null
            }) {
                Text(text = "Отмена")
            }

        })
    }
}