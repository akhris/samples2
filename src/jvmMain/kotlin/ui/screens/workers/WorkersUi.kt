package ui.screens.workers

import androidx.compose.foundation.layout.Column
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WorkersUi(component: IWorkers) {
    val state by component.state.subscribeAsState()

    //render workers list:
    Column {
        state.rooms.forEach { worker ->
            ListItem(overlineText = {
                Text(worker.email)
            }, text = {
                Text("${worker.name} ${worker.surname}")
            }, secondaryText = {
                Text("${worker.phoneNumber} ${worker.email}")
            })
        }
    }
}