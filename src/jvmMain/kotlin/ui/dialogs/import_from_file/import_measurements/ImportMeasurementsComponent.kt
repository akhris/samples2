package ui.dialogs.import_from_file.import_measurements

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.reduce
import com.arkivanov.essenty.lifecycle.subscribe
import domain.*
import domain.application.Result
import domain.application.baseUseCases.GetEntities
import domain.application.baseUseCases.InsertEntity
import kotlinx.coroutines.*
import org.kodein.di.DI
import org.kodein.di.instance
import persistence.export_import.json.application.ImportFromJSON
import persistence.export_import.json.dto.JSONMeasurement
import persistence.export_import.json.dto.JSONMeasurementResult
import persistence.export_import.json.serializers.LocalDateTimeSerializer
import ui.dialogs.import_from_file.ImportFromFileComponent
import ui.screens.base_entity_screen.entityComponents.FileExtensions
import utils.log
import java.nio.file.Path
import java.time.LocalDateTime
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.extension
import kotlin.io.path.isDirectory

class ImportMeasurementsComponent(
    private val filePath: String,
    private val di: DI,
    componentContext: ComponentContext
) : IImportMeasurements, ComponentContext by componentContext {

    private val scope =
        CoroutineScope(Dispatchers.Default + SupervisorJob())


    private val getSampleTypes: GetEntities<SampleType> by di.instance()
    private val getSamples: GetEntities<Sample> by di.instance()
    private val getOperators: GetEntities<Worker> by di.instance()
    private val getPlaces: GetEntities<Place> by di.instance()
    private val getParameters: GetEntities<Parameter> by di.instance()
    private val importFromJSON: ImportFromJSON<Measurement, JSONMeasurement> by di.instance()

    private val insertSample: InsertEntity<Sample> by di.instance()

    private val _state = MutableValue(IImportMeasurements.State())

    override val state: Value<IImportMeasurements.State> = _state


    private fun checkFile(): Boolean {
        val file = Path(filePath)
        return !(!file.exists() || file.isDirectory())
    }

    private suspend fun invalidateSampleTypes() {
        val result = getSampleTypes(GetEntities.Params.GetWithSpecification(Specification.QueryAll))
        when (result) {
            is Result.Failure -> {
                log(result.throwable)
            }

            is Result.Success -> {
                _state.reduce { it.copy(types = result.value.flatten()) }
            }
        }
    }

    private fun importFromFile() {
        val file = Path(filePath)
        if (!checkFile()) return
        _state.reduce { it.copy(filePath = filePath) }
        scope.launch {
            invalidateSampleTypes()

            when (file.extension) {
                in FileExtensions.JSON.extensions -> {
                    importFromJSONFile(file)
                }

                in FileExtensions.EXCEL.extensions -> {
                    importFromEXCELFile(file)
                }
            }
        }
    }


    private suspend fun importFromJSONFile(file: Path) {
        // TODO: //show import dialog to choose sample types, workers, e.t.c.
        //make actual read from JSON file
        when (val imported = importFromJSON(ImportFromJSON.Params.ImportFromFile(file.toString()))) {
            is Result.Failure -> {
//                showErrorDialog(
//                    title = "Error while importing from JSON file",
//                    caption = file.toString(),
//                    error = imported.throwable
//                )
            }

            is Result.Success -> {
                invalidateJSONMeasurements(imported.value)
            }
        }
    }

    private suspend fun invalidateJSONMeasurements(jsonMeasurements: List<JSONMeasurement>) {
        //1. try to map JSONMeasurement:
        val mapped = jsonMeasurements.map { it.map() }
    }

    // map to measurement:
    private suspend fun JSONMeasurement.map(): Measurement {
        
        val sample = sample?.let { s ->
            when (val samples = getSamples(GetEntities.Params.GetWithSpecification(Specification.Search(s)))) {
                is Result.Failure -> null
                is Result.Success -> samples.value.flatten().firstOrNull()
            } ?: kotlin.run {
                //if sample was not found - add one (create new)
                val newSample = Sample(identifier = s)
                insertSample(InsertEntity.Insert(newSample))
                newSample
            }
        }


        val operator = operator?.let { o ->
            when (val operators = getOperators(GetEntities.Params.GetWithSpecification(Specification.Search(o)))) {
                is Result.Failure -> null
                is Result.Success -> operators.value.flatten().firstOrNull()
            }
        } //todo if worker was not found - add one (create new)

        val place = place?.let { p ->
            when (val places = getPlaces(GetEntities.Params.GetWithSpecification(Specification.Search(p)))) {
                is Result.Failure -> null
                is Result.Success -> places.value.flatten().firstOrNull()
            }
        } //todo if place was not found - add one (create new)

        val results = results.mapNotNull { it.map() }

        //todo need to try to determine sample type based on results?

        return Measurement(
            sample = sample,
            dateTime = dateTime?.let { LocalDateTime.parse(it) },
            operator = operator,
            place = place,
            comment = comment,
            conditions = conditions,
            results = results
        )
    }

    private suspend fun JSONMeasurementResult.map(): MeasurementResult? {
        val parameter =
            when (val params =
                getParameters(GetEntities.Params.GetWithSpecification(Specification.Search(parameter)))) {
                is Result.Failure -> null
                is Result.Success -> params.value.flatten().firstOrNull()
            }

        return parameter?.let {
            MeasurementResult(it, value)
        }
    }

    private suspend fun importFromEXCELFile(file: Path) {
        //make actual read from EXCEL file
    }


    init {

        componentContext
            .lifecycle
            .subscribe(onDestroy = {
                scope.coroutineContext.cancelChildren()
            })


        importFromFile()

    }


}