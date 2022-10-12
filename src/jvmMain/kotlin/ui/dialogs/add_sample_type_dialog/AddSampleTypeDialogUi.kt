package ui.dialogs.add_sample_type_dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.rememberDialogState
import domain.SampleType

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddSampleTypeDialogUi(component: IAddSampleTypeDialogComponent, onDismiss: () -> Unit) {

    var newSampleTypeName by remember { mutableStateOf("") }

    AlertDialog(onDismissRequest = onDismiss, text = {
        TextField(
            value = newSampleTypeName,
            onValueChange = { newSampleTypeName = it },
            label = { Text("Имя типа") })
    }, confirmButton = {
        Button(onClick = {
            if (newSampleTypeName.isNotEmpty()) {
                val newSampleType = SampleType(name = newSampleTypeName)
                component.addSampleType(
                    newSampleType
                )
                onDismiss()
            }
        }, content = { Text("Добавить") })
    }, title = {
        Text("Новый тип образцов")
    })


}