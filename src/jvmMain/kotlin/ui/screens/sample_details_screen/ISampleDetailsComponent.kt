package ui.screens.sample_details_screen

import com.arkivanov.decompose.value.Value
import domain.Operation
import domain.Sample

interface ISampleDetailsComponent {
    val stateSample: Value<Sample>
    val stateOperations: Value<List<Operation>>
}