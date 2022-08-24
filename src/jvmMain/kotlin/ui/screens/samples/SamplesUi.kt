package ui.screens.samples

import LocalSamplesType
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import domain.Sample

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SamplesUi(component: ISamples) {
    val state by component.state.subscribeAsState()

    val currentType = LocalSamplesType.current
    //render samples list:
    Column {
        // add sample row:
        AddSampleBlock(onNewSampleAdd = { id ->
            currentType?.let { type ->
                component.insertNewSample(Sample(id = id, type = type))
            }
        })

        state
            .samples
            .filter { it.type == currentType }
            .forEach { sample ->
                ListItem(overlineText = {
                    Text(sample.id)
                }, text = {
                    Text("${sample.description}")
                }, secondaryText = {

                })
            }
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