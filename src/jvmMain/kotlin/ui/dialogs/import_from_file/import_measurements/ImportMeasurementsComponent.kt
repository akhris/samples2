package ui.dialogs.import_from_file.import_measurements

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.*
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.reduce
import com.arkivanov.essenty.lifecycle.subscribe
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
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
import ui.dialogs.edit_sample_type_dialog.EditSampleTypeDialogComponent
import ui.dialogs.import_from_file.IImportFromFile
import ui.dialogs.import_from_file.ImportFromFileComponent
import ui.screens.base_entity_screen.entityComponents.FileExtensions
import ui.utils.sampletypes_selector.SampleTypesSelectorComponent
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

    private val sampleTypesNav = StackNavigation<TypesSelectorConfig>()

    private val _typesStack = childStack(
        source = sampleTypesNav,
        initialConfiguration = TypesSelectorConfig.SampleTypesSelector,
        childFactory = ::createChild,
        key = "sample types selector"
    )

    override val sampleTypesStack: Value<ChildStack<*, IImportMeasurements.SampleTypesUtils>> = _typesStack

    private val getSampleTypes: GetEntities<SampleType> by di.instance()
    private val getSamples: GetEntities<Sample> by di.instance()
    private val getOperators: GetEntities<Worker> by di.instance()
    private val getPlaces: GetEntities<Place> by di.instance()
    private val getParameters: GetEntities<Parameter> by di.instance()
    private val importFromJSON: ImportFromJSON<Measurement, JSONMeasurement> by di.instance()

    private val insertSample: InsertEntity<Sample> by di.instance()

    private val _jsonMeasurements = MutableValue(listOf<JSONMeasurement>())

    private val _state = MutableValue(IImportMeasurements.State())

    override val state: Value<IImportMeasurements.State> = _state


    private fun createChild(
        config: TypesSelectorConfig,
        componentContext: ComponentContext
    ): IImportMeasurements.SampleTypesUtils {
        return when (config) {
            TypesSelectorConfig.SampleTypesSelector -> IImportMeasurements.SampleTypesUtils.SampleTypesSelector(
                component = SampleTypesSelectorComponent(di = di, componentContext = componentContext)
            )

            is TypesSelectorConfig.EditSampleTypesDialog -> IImportMeasurements.SampleTypesUtils.EditSampleTypesDialog(
                component = EditSampleTypeDialogComponent(
                    di = di,
                    componentContext = componentContext,
                    initialSampleType = config.sampleType
                )
            )
        }

    }

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

    private suspend fun importFromFile() {
        val file = Path(filePath)
        if (!checkFile()) return
        _state.reduce { it.copy(filePath = filePath) }

        when (file.extension) {
            in FileExtensions.JSON.extensions -> {
                importFromJSONFile(file)
            }

            in FileExtensions.EXCEL.extensions -> {
                importFromEXCELFile(file)
            }
        }
    }

    override fun selectSampleType(type: SampleType) {
        _state.reduce {
            it.copy(selectedType = type)
        }
        scope.launch {
            invalidateJSONMeasurements()
        }
    }

    override fun editSampleType(type: SampleType?) {
        sampleTypesNav.push(TypesSelectorConfig.EditSampleTypesDialog(type))
    }

    override fun dismissEditSampleType() {
        sampleTypesNav.pop()
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
                _jsonMeasurements.reduce {
                    imported.value
                }
                invalidateJSONMeasurements()
            }
        }
    }

    private suspend fun invalidateJSONMeasurements() {
        val jsonMeasurements = _jsonMeasurements.value
        //1. try to determine sample type based on parameters set
        determineSampleType(jsonMeasurements)
        //try to map JSONMeasurement:


//        val mapped = jsonMeasurements.map { it.map() }
    }

    private suspend fun determineSampleType(jsonMeasurements: List<JSONMeasurement>) {
        // 1. get all unique parameters identifiers from json:
        val paramsSet = jsonMeasurements.flatMap {
            it.results.map { it.parameter }
        }.distinct()
        log("paramsSet:\n$paramsSet")

        // 2. get all parameters from db:
        val paramsFromDB =
            when (val result = getParameters(GetEntities.Params.GetWithSpecification(Specification.QueryAll))) {
                is Result.Failure -> {
                    log("all parameters was not loaded from database: ${result.throwable.localizedMessage}")
                    listOf()
                }

                is Result.Success -> {
                    result.value.flatten()
                }
            }
        log("paramsFromDB:\n${paramsFromDB}")
        // 3. associate params set by its sample type from db:
        val associatedParams =
            paramsSet.groupBy { jsonParam ->
                paramsFromDB.find { it.name == jsonParam }?.sampleType
            }
        log("associatedParams:\n${associatedParams}")

        val determinedSampleType = _state.value.selectedType ?: associatedParams.maxByOrNull { it.value.size }?.key
        log("determinedSampleType:\n${determinedSampleType}")

        val parametersToAdd = if (determinedSampleType != null) {
            paramsSet.minus((associatedParams[determinedSampleType] ?: listOf()).toSet())
        } else {
            paramsSet
        }

        log("parametersToAdd:\n${parametersToAdd}")
        _state.reduce {
            it.copy(
                selectedType = determinedSampleType,
                parametersToAdd = parametersToAdd
            )
        }

    }

    // map to measurement:
    /*
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

     */

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

    @Parcelize
    private sealed class TypesSelectorConfig : Parcelable {
        @Parcelize
        object SampleTypesSelector : TypesSelectorConfig()

        @Parcelize
        class EditSampleTypesDialog(val sampleType: SampleType?) : TypesSelectorConfig()
    }

    init {

        componentContext
            .lifecycle
            .subscribe(onDestroy = {
                scope.coroutineContext.cancelChildren()
            })

        scope.launch {
            invalidateSampleTypes()
            importFromFile()
        }

    }


}