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

@Composable
fun OperationsUi(component: IOperations) {
    var showAddOperationDialog by remember { mutableStateOf(false) }



    Box(modifier = Modifier.fillMaxSize()) {


        //parameters table:
//        BaseTable(adapter)

        FloatingActionButton(
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            onClick = {
                //todo show add sample dialog
                showAddOperationDialog = true
            },
            content = { Icon(Icons.Rounded.Add, contentDescription = "add operation") })
    }
}

