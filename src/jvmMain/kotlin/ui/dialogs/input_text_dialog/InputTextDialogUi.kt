package ui.dialogs.input_text_dialog

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun InputTextDialogUi(component: IInputTextDialog, onDismiss: () -> Unit) {

    val state by remember(component) { component.state }.subscribeAsState()


    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            TextField(value = state.text, onValueChange = { component.changeText(it) }, label = { Text(state.caption) })
        },
        title = { Text(text = state.title) },
        confirmButton = {
            Button(onClick = {
                component.confirm()
                onDismiss()
            }) {
                Text("Ок")
            }
        }
    )
}