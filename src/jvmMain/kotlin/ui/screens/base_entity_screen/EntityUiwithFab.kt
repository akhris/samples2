package ui.screens.base_entity_screen

import LocalSamplesType
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import domain.IEntity
import ui.components.tables.SelectionMode

/**
 * Wrapper of BaseEntityUi with FloatingActionButton which adds single entity to the table
 */
@Composable
fun <T : IEntity> EntityUiwithFab(
    component: IEntityComponent<T>,
    selectionMode: SelectionMode<T> = SelectionMode.Multiple()
) {

    val sampleType = LocalSamplesType.current

    Box(modifier = Modifier.fillMaxSize()) {
        BaseEntityUi(
            modifier = Modifier.align(Alignment.TopCenter).padding(end = 48.dp),
            component = component,
            selectionMode = selectionMode
        )

        FloatingActionButton(
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            onClick = {
                //add entity:
                sampleType?.let {
                    component.insertNewEntity(it)
                }
                //todo show add sample dialog
            },
            content = { Icon(Icons.Rounded.Add, contentDescription = "add parameter") })
    }

}