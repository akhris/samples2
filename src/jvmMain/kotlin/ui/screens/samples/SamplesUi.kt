package ui.screens.samples

import LocalSamplesType
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import domain.Sample
import ui.components.tables.BaseTable
import ui.components.tables.SamplesAdapter

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SamplesUi(component: ISamples) {
    val state by component.state.subscribeAsState()

    val currentType = LocalSamplesType.current
    //render samples list:
    val samples = remember(state) { state.samples }
    val adapter = remember(samples) {
        SamplesAdapter(samples, onEntityChanged = {
            component.updateSample(it)
        })
    }
    Box(modifier = Modifier.fillMaxSize()) {


        //parameters table:
        BaseTable(adapter)

        FloatingActionButton(
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            onClick = {
                      //todo show add sample dialog
//                showAddParameterDialog = true
                      },
            content = { Icon(Icons.Rounded.Add, contentDescription = "add parameter") })
    }
}

@Composable
private fun AddSampleBlock(onNewSampleAdd: (String) -> Unit) {

    var sampleID by remember { mutableStateOf("") }

    Row(modifier = Modifier.fillMaxWidth()) {
        TextField(modifier = Modifier.weight(1f), value = sampleID, onValueChange = { sampleID = it })
        Button(onClick = {
            onNewSampleAdd(sampleID)
        }) {
            Text("добавить")
        }
    }
}