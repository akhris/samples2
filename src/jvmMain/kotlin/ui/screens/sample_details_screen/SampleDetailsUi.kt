package ui.screens.sample_details_screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import domain.Operation
import domain.Sample
import utils.DateTimeConverter

@Composable
fun SampleDetailsUi(component: ISampleDetailsComponent) {

    val stateSample by remember(component) { component.stateSample }.subscribeAsState()
    val stateOperations by remember(component) { component.stateOperations }.subscribeAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        SampleDetailsContent(sample = stateSample, onSampleChange = {}, operations = stateOperations)
    }

}


@Composable
private fun BoxScope.SampleDetailsContent(
    sample: Sample,
    onSampleChange: (Sample) -> Unit,
    operations: List<Operation>
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        //Sample info:
        SampleContent(sample, onSampleChange)

        //Operations:
        OperationsContent(operations)
    }
}

@Composable
private fun ColumnScope.SampleContent(sample: Sample, onSampleChange: (Sample) -> Unit) {
    Card(modifier = Modifier.padding(16.dp)) {
        Column {
            OutlinedTextField(
                value = sample.identifier ?: "",
                onValueChange = { onSampleChange(sample.copy(identifier = it)) },
                label = {
                    Text("Идентификатор")
                }
            )
        }
    }
}

@Composable
private fun ColumnScope.OperationsContent(operations: List<Operation>) {
    Card(modifier = Modifier.padding(16.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Операции:", style = MaterialTheme.typography.h5)
            operations.forEachIndexed { index, op ->
                Text("${index + 1}. ${op.operationType?.name} ${op.dateTime?.let { DateTimeConverter.dateTimeToString(it) } ?: ""} ${op.worker?.surname ?: ""} ${op.place?.roomNumber ?: ""}")
            }
        }
    }
}