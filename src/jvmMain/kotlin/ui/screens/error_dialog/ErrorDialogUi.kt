package ui.screens.error_dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ErrorDialogUi(component: IErrorDialogComponent, onDismissDialog: () -> Unit) {

    val state by remember(component) { component.state }.subscribeAsState()

    AlertDialog(onDismissRequest = onDismissDialog, title = { Text(state.title) }, text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (state.caption.isNotEmpty()) {
                Text(text = state.caption, style = MaterialTheme.typography.caption)
            }

            state.error?.let { t ->
                Text(text = t.localizedMessage)
            }
        }
    }, confirmButton = {
        Button(onClick = onDismissDialog) {
            Text("ОК")
        }
    })

}