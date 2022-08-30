package ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.rememberDialogState
import domain.Sample
import domain.SampleType
import ui.UiSettings

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun SampleTypeSelector(
    modifier: Modifier = Modifier,
    typesList: List<SampleType>,
    selectedType: SampleType?,
    onSampleTypeSelected: (SampleType) -> Unit,
    onNewSampleTypeAdd: (SampleType) -> Unit,
    onSampleTypeDelete: (SampleType) -> Unit
) {

    var isMenuOpened by remember(selectedType) { mutableStateOf(false) }
    val rotation by animateFloatAsState(if (isMenuOpened) 180f else 0f)

    var showNewSampleTypeDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf<SampleType?>(null) }
    Box(modifier = modifier) {
        if (selectedType != null) {
            ListItem(
                modifier = Modifier.fillMaxWidth(),
                text = {
                    Text(text = selectedType.name)
                }, secondaryText = {
                    Text(text = selectedType.description)
                }, trailing = {
                    IconButton(onClick = {
                        isMenuOpened = !isMenuOpened
                    }) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowDropDown,
                            modifier = Modifier.size(UiSettings.SampleTypesSelector.dropDownIconSize).rotate(rotation),
                            contentDescription = "open drop-down"
                        )
                    }
                })
        } else {
            //selected type == null
            ListItem(
                modifier = Modifier.fillMaxWidth(),
                text = {
                    Text(text = "добавить тип прибора")
                }, trailing = {
                    IconButton(onClick = {
                       showNewSampleTypeDialog = true
                    }) {
                        Icon(
                            Icons.Rounded.AddCircle,
                            modifier = Modifier.padding(8.dp),
                            contentDescription = "add new sample type"
                        )
                    }
                })
        }

        if (isMenuOpened) {
            DropdownMenu(
                expanded = true,
                onDismissRequest = {
                    isMenuOpened = false
                }
            ) {
                typesList.forEach { sampleType ->

                    var isHover by remember { mutableStateOf(false) }

                    DropdownMenuItem(

                        onClick = {
                            onSampleTypeSelected(sampleType)
                        }) {
                        Text(modifier = Modifier.weight(1f), text = sampleType.name)

                        Icon(
                            modifier = Modifier.onPointerEvent(PointerEventType.Enter) { isHover = true }
                                .onPointerEvent(PointerEventType.Exit) { isHover = false }
                                .clickable {
                                    showDeleteConfirmDialog = sampleType
                                },
                            imageVector = Icons.Rounded.Clear,
                            contentDescription = "delete ${sampleType.name}",
                            tint = MaterialTheme.colors.error.copy(alpha = if (isHover) 1.0f else 0.2f)
                        )
                    }
                }
                DropdownMenuItem(onClick = { showNewSampleTypeDialog = true }) {
                    Icon(
                        Icons.Rounded.AddCircle,
                        modifier = Modifier.padding(8.dp),
                        contentDescription = "add new sample type"
                    )
                    Text(modifier = Modifier.weight(1f), text = "добавить тип прибора")
                }
            }
        }
    }

    if (showNewSampleTypeDialog) {

        var newSampleTypeName by remember { mutableStateOf("") }

        Dialog(
            state = rememberDialogState(),
            onCloseRequest = { showNewSampleTypeDialog = false },
            content = {
                Column {
                    TextField(
                        value = newSampleTypeName,
                        onValueChange = { newSampleTypeName = it },
                        label = { Text("Имя типа образцов") })
                    Button(onClick = {
                        if (newSampleTypeName.isNotEmpty()) {
                            onNewSampleTypeAdd(
                                SampleType(name = newSampleTypeName)
                            )
                            showNewSampleTypeDialog = false
                        }
                    }, content = { Text("Добавить") })
                }
            })
    }

    showDeleteConfirmDialog?.let { sampleType ->
        AlertDialog(onDismissRequest = {
            showDeleteConfirmDialog = null
        }, confirmButton = {
            Button(
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.error),
                onClick = {
                    onSampleTypeDelete(sampleType)
                    showDeleteConfirmDialog = null
                }) {
                Text(text = "Удалить", color = MaterialTheme.colors.onError)
            }
        }, text = {
            Text(text = "Удалить тип образцов ${sampleType.name}?")
        }, dismissButton = {
            OutlinedButton(onClick = {
                showDeleteConfirmDialog = null
            }) {
                Text(text = "Отмена")
            }

        })
    }

}