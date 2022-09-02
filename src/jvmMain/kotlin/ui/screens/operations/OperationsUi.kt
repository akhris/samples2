package ui.screens.operations

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import domain.Operation
import ui.components.tables.DataTable
import ui.components.tables.mappers.OperationsDataMapper
import utils.log

@Composable
fun OperationsUi(component: IOperations) {
    val state by remember(component) { component.state }.subscribeAsState()

    var showAddOperationDialog by remember { mutableStateOf(false) }

    log("operations: ${state.operations}")

    val mapper = remember { OperationsDataMapper() }


    Box(modifier = Modifier.fillMaxSize()) {


        //parameters table:
        DataTable(
            modifier = Modifier.align(Alignment.TopCenter).padding(end = 80.dp),
            items = state.operations,
            mapper = mapper,
            onItemChanged = {
                component.updateOperation(it)
            })

        FloatingActionButton(
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            onClick = {
                //todo show add sample dialog
                component.insertOperation(Operation())
//                showAddOperationDialog = true
            },
            content = { Icon(Icons.Rounded.Add, contentDescription = "add operation") })
    }
}

