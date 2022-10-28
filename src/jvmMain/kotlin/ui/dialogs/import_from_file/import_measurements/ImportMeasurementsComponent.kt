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
import domain.application.baseUseCases.InsertEntities
import domain.application.baseUseCases.InsertEntity
import kotlinx.coroutines.*
import org.kodein.di.DI
import org.kodein.di.instance
import persistence.export_import.json.application.ImportFromJSON
import persistence.export_import.json.dto.JSONMeasurement
import persistence.export_import.json.dto.JSONMeasurementResult
import persistence.exposed.dto.Tables
import ui.dialogs.edit_sample_type_dialog.EditSampleTypeDialogComponent
import ui.screens.base_entity_screen.entityComponents.FileExtensions
import ui.utils.sampletypes_selector.SampleTypesSelectorComponent
import utils.log
import utils.toFormattedList
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

    private val insertSamples: InsertEntities<Sample> by di.instance()
    private val insertParameters: InsertEntities<Parameter> by di.instance()
    private val insertOperators: InsertEntities<Worker> by di.instance()
    private val insertPlaces: InsertEntities<Place> by di.instance()
    private val insertMeasurements: InsertEntities<Measurement> by di.instance()


    private val _state = MutableValue(IImportMeasurements.State())

    override val state: Value<IImportMeasurements.State> = _state

    private val _processingState =
        MutableValue<IImportMeasurements.ProcessingState>(IImportMeasurements.ProcessingState.IDLE)

    override val processingState: Value<IImportMeasurements.ProcessingState> = _processingState

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

    private fun setProgress(progress: Float?, caption: String) {
        _processingState.reduce {
            IImportMeasurements.ProcessingState.InProgress(progress = progress, caption = caption)
        }
    }

    override fun editSampleType(type: SampleType?) {
        sampleTypesNav.push(TypesSelectorConfig.EditSampleTypesDialog(type))
    }

    override fun dismissEditSampleType() {
        sampleTypesNav.pop()
    }

    override fun storeImportedMeasurements() {
        //make actual saving to database:
        //1. save all entities that are going to be saved:
        scope.launch {

            _state.value.apply {
                val type = selectedType ?: return@launch
                val samples = samplesToAdd.associateWith { Sample(identifier = it, type = type) }
                val parameters = parametersToAdd.associateWith { Parameter(name = it, sampleType = type) }
                val operators = workersToAdd.associateWith { Worker(surname = it) }
                val places = placesToAdd.associateWith { Place(roomNumber = it) }

//                log("going to insert in db:")
//                log("samples:\n${samples.values.toList().toFormattedList()}")
//                log("parameters:\n${parameters.values.toList().toFormattedList()}")
//                log("operators:\n${operators.values.toList().toFormattedList()}")
//                log("places:\n${places.values.toList().toFormattedList()}")

                //insert them in database:
                setProgress(progress = 0f, caption = "Импорт образцов...")
                insertSamples(InsertEntities.Insert(samples.values.toList()))
                delay(200L)
                setProgress(progress = 0.25f, caption = "Импорт параметров...")
                insertParameters(InsertEntities.Insert(parameters.values.toList()))
                delay(200L)
                setProgress(progress = 0.50f, caption = "Импорт операторов...")
                insertOperators(InsertEntities.Insert(operators.values.toList()))
                delay(200L)
                setProgress(progress = 0.75f, caption = "Импорт мест...")
                insertPlaces(InsertEntities.Insert(places.values.toList()))
                delay(200L)
                //map JSONResults to results:


                //make list of domain measurements:
                val measurements = JSONMeasurements
                    .map {
                        Measurement(
                            sample = it.sample?.let { s -> samples[s] },
                            sampleType = type,
                            dateTime = it.dateTime?.let { dt -> LocalDateTime.parse(dt) },
                            operator = it.operator?.let { o -> operators[o] },
                            place = it.place?.let { p -> places[p] },
                            comment = it.comment,
                            conditions = it.conditions,
                            results = it.results.mapNotNull { jsResult ->
                                log("mapping jsResult: $jsResult")
                                parameters[jsResult.parameter]?.let { p ->
                                    MeasurementResult(parameter = p, value = jsResult.value)
                                }
                            }
                        )
                    }
                setProgress(progress = 1f, caption = "Импорт измерений...")
                insertMeasurements(InsertEntities.Insert(measurements))
                _processingState.reduce { IImportMeasurements.ProcessingState.SuccessfullyImported }
            }
        }

    }

    private suspend fun importFromJSONFile(file: Path) {
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
                _state.reduce {
                    it.copy(JSONMeasurements = imported.value)
                }
                invalidateJSONMeasurements()
            }
        }
    }

    private suspend fun invalidateJSONMeasurements() {
        val jsonMeasurements = _state.value.JSONMeasurements
        //parallel running coroutines:
        listOf(
            scope.async(Dispatchers.IO) { determineSampleType(jsonMeasurements) },
            scope.async(Dispatchers.IO) { determineSamples(jsonMeasurements) },
            scope.async(Dispatchers.IO) { determineWorkers(jsonMeasurements) },
            scope.async(Dispatchers.IO) { determinePlaces(jsonMeasurements) })
            .awaitAll()
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

    private suspend fun determineWorkers(jsonMeasurements: List<JSONMeasurement>) {
        // 1. get all unique Operators from json:
        val operatorsSet = jsonMeasurements.mapNotNull {
            it.operator
        }.distinct()

        // 2. get all Operators from db:
        val operatorsFromDB =
            when (val result = getOperators(GetEntities.Params.GetWithSpecification(Specification.QueryAll))) {
                is Result.Failure -> {
                    log("all operators was not loaded from database: ${result.throwable.localizedMessage}")
                    listOf()
                }

                is Result.Success -> {
                    result.value.flatten()
                }
            }

        // 3. associate Operators set by its sample type from db:
        val associatedOperators =
            operatorsSet.groupBy { jsonOperator ->
                operatorsFromDB.find { it.formatName().contains(jsonOperator) }
            }

        val workersToAdd = associatedOperators[null] ?: listOf()
        _state.reduce {
            it.copy(workersToAdd = workersToAdd)
        }
    }


    private suspend fun determinePlaces(jsonMeasurements: List<JSONMeasurement>) {
        // 1. get all unique Operators from json:
        val placesSet = jsonMeasurements.mapNotNull {
            it.place
        }.distinct()

        // 2. get all Operators from db:
        val placesFromDB =
            when (val result = getPlaces(GetEntities.Params.GetWithSpecification(Specification.QueryAll))) {
                is Result.Failure -> {
                    log("all places was not loaded from database: ${result.throwable.localizedMessage}")
                    listOf()
                }

                is Result.Success -> {
                    result.value.flatten()
                }
            }

        // 3. associate Operators set by its sample type from db:
        val associatedPlaces =
            placesSet.groupBy { jsonPlace ->
                placesFromDB.find { (it.name == jsonPlace) || (it.description == jsonPlace) || (it.roomNumber == jsonPlace) }
            }

        val placesToAdd = associatedPlaces[null] ?: listOf()
        _state.reduce {
            it.copy(placesToAdd = placesToAdd)
        }
    }

    private suspend fun determineSamples(jsonMeasurements: List<JSONMeasurement>) {
        // 1. get all unique Operators from json:
        val samplesSet = jsonMeasurements.mapNotNull {
            it.sample
        }.distinct()

        // 2. get all Operators from db:
        val samplesFromDB =
            when (val result = getSamples(
                GetEntities.Params.GetWithSpecification(
                    Specification.Filtered(
                        listOf(
                            FilterSpec.Values(
                                columnName = Tables.Samples.sampleType.name,
                                filteredValues = listOfNotNull(_state.value.selectedType?.id)
                            )
                        )
                    )
                )
            )) {
                is Result.Failure -> {
                    log("all places was not loaded from database: ${result.throwable.localizedMessage}")
                    listOf()
                }

                is Result.Success -> {
                    result.value.flatten()
                }
            }

        // 3. associate Operators set by its sample type from db:
        val associatedSamples =
            samplesSet.groupBy { jsonSample ->
                samplesFromDB.find { it.identifier == jsonSample }
            }

        val samplesToAdd = associatedSamples[null] ?: listOf()
        _state.reduce {
            it.copy(samplesToAdd = samplesToAdd)
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