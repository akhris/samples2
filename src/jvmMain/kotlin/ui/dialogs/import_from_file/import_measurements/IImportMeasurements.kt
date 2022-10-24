package ui.dialogs.import_from_file.import_measurements

import com.arkivanov.decompose.value.Value
import domain.Measurement

interface IImportMeasurements {

    val state: Value<State>

    data class State(
        val filePath: String = "",
        val measurements: List<Measurement> = listOf()
    )
}