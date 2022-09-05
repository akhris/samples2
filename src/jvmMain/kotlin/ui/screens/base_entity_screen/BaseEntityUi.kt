package ui.screens.base_entity_screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import domain.IEntity
import ui.components.tables.DataTable
import ui.components.tables.SelectionMode

@Composable
fun <T : IEntity> BaseEntityUi(
    component: IEntityComponent<T>,
    selectionMode: SelectionMode = SelectionMode.Multiple,
    onSelectionChanged: ((List<String>) -> Unit)? = null
) {
    val state by component.state.subscribeAsState()

    //render samples list:
    val entities = remember(state) { state.entities }


    Box(modifier = Modifier.fillMaxSize()) {

        //parameters table:
        DataTable(
            modifier = Modifier.align(Alignment.TopCenter).padding(end = 80.dp),
            items = entities,
            mapper = component.dataMapper,
            onItemChanged = {
                component.updateEntity(it)
            },
            onCellClicked = { item, cell ->

            },
            selectionMode = selectionMode,
            onSelectionChanged = onSelectionChanged,
            onEntityPickerClicked = {

            }
        )


        FloatingActionButton(
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            onClick = {
                //todo show add sample dialog
            },
            content = { Icon(Icons.Rounded.Add, contentDescription = "add parameter") })
    }

}