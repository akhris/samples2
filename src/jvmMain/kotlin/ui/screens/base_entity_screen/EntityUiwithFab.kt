package ui.screens.base_entity_screen

import LocalSamplesType
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import domain.IEntity
import ui.components.IconResource
import ui.components.multiFAB.MultiFabItem
import ui.components.multiFAB.MultiFabState
import ui.components.multiFAB.MultiFloatingActionButton

/**
 * Wrapper of BaseEntityUi with FloatingActionButton which adds single entity to the table
 */
@Composable
fun <T : IEntity> EntityUiwithFab(
    component: EntityComponentWithFab<T>
) {

    val sampleType = LocalSamplesType.current
    var toState by remember { mutableStateOf(MultiFabState.COLLAPSED) }

    Box(modifier = Modifier.fillMaxSize()) {
        BaseEntityUi(
            modifier = Modifier.align(Alignment.TopCenter).padding(end = 48.dp),
            component = component
        )

        val items = component
            .getFabParams()
            .map {
                MultiFabItem(icon = it.icon, identifier = it.id, label = it.label ?: "")
            }


        MultiFloatingActionButton(
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            fabIcon = IconResource.ImageVectorIcon(Icons.Rounded.Add),
            items = items,
            toState = toState,
            stateChanged = { toState = it },
            onFabItemClicked = {
                component.invokeFABAction(it.identifier, sampleType)
            }
        )
//
//        FloatingActionButton(
//            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
//            onClick = {
//                //add entity:
//                sampleType?.let {
//                    component.insertNewEntity(it)
//                }
//                //todo show add sample dialog
//            },
//            content = { Icon(Icons.Rounded.Add, contentDescription = "add parameter") })
    }

}