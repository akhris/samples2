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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import domain.IEntity
import ui.components.tables.BaseTable

@Composable
fun <T : IEntity> EntityGridUi(component: IEntityComponent<T>) {
    val state by component.state.subscribeAsState()

    val currentType = LocalSamplesType.current
    //render samples list:
    val entities = remember(state) { state.entities }
    val adapter = remember(entities) {
        // FIXME: getadapter from di or factory
//        SamplesAdapter(samples, onEntityChanged = {
//            component.updateSample(it)
//        })
    }

    Box(modifier = Modifier.fillMaxSize()) {


        //parameters table:
        BaseTable(adapter)

        FloatingActionButton(
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            onClick = {
                //todo show add sample dialog
//                showAddSampleDialog = true
            },
            content = { Icon(Icons.Rounded.Add, contentDescription = "add parameter") })
    }

}