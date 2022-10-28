package ui.dialogs.import_from_file.import_measurements

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import domain.*
import persistence.export_import.json.dto.JSONMeasurement
import ui.dialogs.edit_sample_type_dialog.IEditSampleTypeDialogComponent
import ui.utils.sampletypes_selector.ISampleTypesSelector

interface IImportMeasurements {

    val state: Value<State>

    val processingState: Value<ProcessingState>

    val sampleTypesStack: Value<ChildStack<*, SampleTypesUtils>>
    fun selectSampleType(type: SampleType)

    fun editSampleType(type: SampleType?)

    fun dismissEditSampleType()

    fun storeImportedMeasurements()

    data class State(
        val filePath: String = "",
        val types: List<SampleType> = listOf(),
        val selectedType: SampleType? = null,
        val measurements: List<Measurement> = listOf(),

        val JSONMeasurements: List<JSONMeasurement> = listOf(),

        val parametersToAdd: List<String> = listOf(),
        val workersToAdd: List<String> = listOf(),
        val placesToAdd: List<String> = listOf(),
        val samplesToAdd: List<String> = listOf()
    )

    sealed class ProcessingState {
        object IDLE : ProcessingState()

        /**
         * [progress] - importing progress from 0.0f to 1.0f
         * if [progress] is null -> show running progress indicator
         */
        class InProgress(val caption: String, val progress: Float?) : ProcessingState()

        object SuccessfullyImported : ProcessingState()
    }

    sealed class SampleTypesUtils {
        class SampleTypesSelector(val component: ISampleTypesSelector) : SampleTypesUtils()
        class EditSampleTypesDialog(val component: IEditSampleTypeDialogComponent) : SampleTypesUtils()
    }
}