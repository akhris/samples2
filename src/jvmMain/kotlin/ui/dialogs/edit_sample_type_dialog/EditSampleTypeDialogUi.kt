package ui.dialogs.edit_sample_type_dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import ui.dialogs.BaseDialog

@Composable
fun EditSampleTypeDialogUi(component: IEditSampleTypeDialogComponent, onDismiss: () -> Unit) {

    val newSampleType by remember(component) { component.sampleType }.subscribeAsState()
    val dialogType by remember(component) { component.dialogType }.subscribeAsState()
    val isChanged by remember(component) { component.isChanged }.subscribeAsState()

    var isError by remember { mutableStateOf(false) }


    BaseDialog(
        onDismiss = onDismiss,
        title = {
            val title = remember(dialogType) {
                when (dialogType) {
                    IEditSampleTypeDialogComponent.DialogType.Add -> "Новый тип образцов"
                    IEditSampleTypeDialogComponent.DialogType.Edit -> "Изменить тип"
                }
            }
            Text(text = title, modifier = Modifier.padding(16.dp))
        },
        content = {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = newSampleType.name,
                    onValueChange = {
                        component.updateSampleTypeInCache(newSampleType.copy(name = it))
                        isError = it.isEmpty()
                    },
                    label = { Text("Имя") },
                    isError = isError
                )
                if (isError) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Имя не может быть пустым",
                        color = MaterialTheme.colors.error,
                        style = MaterialTheme.typography.caption
                    )
                }

                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = newSampleType.description,
                    onValueChange = { component.updateSampleTypeInCache(newSampleType.copy(description = it)) },
                    label = { Text("Описание") })
            }
        },
        buttons = {
            when (dialogType) {
                IEditSampleTypeDialogComponent.DialogType.Add -> {
                    Button(enabled = !isError,
                        onClick = {
                            if (newSampleType.name.isEmpty()) {
                                isError = true
                            } else {
                                component.insertSampleType(
                                    newSampleType
                                )
                                onDismiss()
                            }
                        }, content = { Text("Добавить") })
                }

                IEditSampleTypeDialogComponent.DialogType.Edit -> {
                    TextButton(
                        onClick = {
                            component.removeSampleType()
                            onDismiss()
                        }, content = { Text("Удалить", color = MaterialTheme.colors.error) })

                    Button(enabled = !isError && isChanged,
                        onClick = {
                            if (newSampleType.name.isEmpty()) {
                                isError = true
                            } else {
                                component.updateSampleTypeInCache(newSampleType)
                                component.updateSampleTypeInStorage(
                                    newSampleType
                                )
                                onDismiss()
                            }
                        }, content = { Text("Изменить") })
                }
            }

        }
    )

}