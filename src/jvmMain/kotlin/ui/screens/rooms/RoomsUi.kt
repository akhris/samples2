package ui.screens.rooms

import androidx.compose.foundation.layout.Column
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RoomsUi(component: IRooms) {
    val state by component.state.subscribeAsState()

    //render rooms list:
    Column {
        state.rooms.forEach {room->
            ListItem(overlineText = {
                Text(room.roomNumber)
            }, text = {
                Text(room.description)
            })
        }
    }
}