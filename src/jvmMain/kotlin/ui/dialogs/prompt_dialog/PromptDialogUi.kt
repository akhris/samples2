package ui.dialogs.prompt_dialog

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PromptDialogUi(component: IPromptDialog, onYes: () -> Unit, onCancel: (() -> Unit)? = null, onDismiss: () -> Unit) {
    val state by remember(component) { component.state }.subscribeAsState()

    AlertDialog(
        title = {
            Text(text = state.title)
        },
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                onYes()
                onDismiss()
            }) {
                Text(text = "Да")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onCancel?.invoke()
                onDismiss()
            }) {
                Text(text = "Нет")
            }
        },
        text = {
            Text(text = state.message)
        }
    )
}