package ui.dialogs.import_from_file.import_measurements

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import domain.*
import persistence.export_import.json.dto.JSONMeasurement
import ui.dialogs.edit_sample_type_dialog.IEditSampleTypeDialogComponent
import ui.dialogs.import_from_file.IImportFromFile
import ui.utils.sampletypes_selector.ISampleTypesSelector

interface IImportMeasurements {

    val state: Value<State>

    val sampleTypesStack: Value<ChildStack<*, SampleTypesUtils>>
    fun selectSampleType(type: SampleType)

    fun editSampleType(type: SampleType?)

    fun dismissEditSampleType()

    fun saveMeasurementsToDB()

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

    sealed class SampleTypesUtils {
        class SampleTypesSelector(val component: ISampleTypesSelector) : SampleTypesUtils()
        class EditSampleTypesDialog(val component: IEditSampleTypeDialogComponent) : SampleTypesUtils()
    }
}