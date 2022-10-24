package ui.screens.base_entity_screen.entityComponents

import com.arkivanov.decompose.ComponentContext
import domain.*
import domain.application.Result
import domain.application.baseUseCases.GetEntities
import kotlinx.coroutines.launch
import org.kodein.di.DI
import org.kodein.di.instance
import persistence.export_import.json.application.ExportToJSON
import persistence.export_import.json.application.ImportFromJSON
import persistence.export_import.json.dto.JSONMeasurement
import ui.components.IconResource
import ui.components.tables.mappers.MeasurementsDataMapper
import ui.dialogs.file_picker_dialog.IFilePicker
import ui.screens.base_entity_screen.EntityComponentWithFab
import ui.screens.base_entity_screen.FABParams
import utils.JavaDesktopUtils
import utils.log
import java.io.File
import javax.swing.filechooser.FileNameExtensionFilter

class MeasurementsEntityComponent(
    di: DI,
    componentContext: ComponentContext
) : EntityComponentWithFab<Measurement>(type = Measurement::class, di = di, componentContext = componentContext) {
    private val getParameters: GetEntities<Parameter> by di.instance()
    private val parametersCallbacks: IRepositoryCallback<Parameter> by di.instance()
    private val getMeasurements: GetEntities<Measurement> by di.instance()

    private val exportToJSON: ExportToJSON<Measurement, JSONMeasurement> by di.instance()
    private val importFromJSON: ImportFromJSON<Measurement, JSONMeasurement> by di.instance()

    override fun getFabParams(): List<FABParams> = listOf(
        FABParams(
            ACTION_ADD_SINGLE,
            icon = IconResource.PainterResourceIcon("vector/plus_one_black_24dp.svg"),
            label = "Добавить запись"
        ),
        FABParams(
            ACTION_ADD_MULTIPLE,
            icon = IconResource.PainterResourceIcon("vector/playlist_add_black_24dp.svg"),
            label = "Добавить несколько"
        ),
        FABParams(
            ACTION_IMPORT_FROM_FILE,
            icon = IconResource.PainterResourceIcon("vector/file_download_black_24dp.svg"),
            label = "Импортировать из файла"
        )
    )




    override fun shareEntities(entities: List<Measurement>) {

        val saveAsExcelID = "save_as_excel"
        val saveAsJSONID = "save_as_json"

        showFilePickerDialog(
            title = "Выберите файл для сохранения данных",
            fileFilters = listOf(
                FileExtensions.JSON,
                FileExtensions.EXCEL
            ).map { it.toFileNameExtensionsFilter() },
            onFileSelectedCallback = { file -> exportToFile(file, entities) },
            pickerType = IFilePicker.PickerType.SaveFile
        )
    }


    override fun invokeFABAction(id: String, tag: Any?) {
        when (id) {
            ACTION_ADD_SINGLE -> {
                (tag as? SampleType)?.let { st ->
                    insertNewEntity(st)
                }
            }

            ACTION_ADD_MULTIPLE -> {

            }

            ACTION_IMPORT_FROM_FILE -> {
                showFilePickerDialog(
                    title = "Выберите файл с результатами измерений",
                    fileFilters =
                    listOf(FileExtensions.JSON, FileExtensions.EXCEL)
                        .map { it.toFileNameExtensionsFilter() },
                    onFileSelectedCallback = {
                        showImportEntityDialog(Measurement::class, filePath = it.path)
                    },
                    pickerType = IFilePicker.PickerType.OpenFile
                )
            }
        }
    }

    private fun checkFile(file: File, isOpen: Boolean): Boolean {
        if (isOpen && !file.exists()) {
            showErrorDialog(title = "Файл не существует", error = NoSuchFileException(file))
            return false
        }

        if (file.isDirectory) {
            showErrorDialog(
                title = "Выбранный путь является директорией",
                error = NoSuchFileException(file, reason = "Необходимо выбрать файл")
            )
            return false
        }
        return true
    }




    private fun exportToFile(file: File, measurements: List<Measurement>) {
        if (!checkFile(file, isOpen = false)) return
        scope.launch {
            when (file.extension) {
                in FileExtensions.JSON.extensions -> exportToJSONFile(file, measurements)
                in FileExtensions.EXCEL.extensions -> exportToExcelFile(file, measurements)
                else -> showErrorDialog(title = "Неподдерживаемый формат файла", error = FileSystemException(file))
            }
        }
    }


    private suspend fun exportToJSONFile(file: File, items: List<Measurement>) {
        log("going to export to JSON file: $file measurements with count: ${items.size}")
        val result = exportToJSON(ExportToJSON.Params.ExportToFile(filePath = file.toString(), entities = items))
        when (result) {
            is Result.Failure -> {
                showErrorDialog(
                    title = "Error while exporting to JSON file",
                    caption = file.toString(),
                    error = result.throwable
                )
            }

            is Result.Success -> {
                showPrompt(
                    title = "Результаты экспорта",
                    message = "${items.size} результатов измерений записаны в файл $file\nОткрыть файл?",
                    {
                        //trying to open just saved file:
                        JavaDesktopUtils.edit(file)
                    }
                )
            }
        }
    }

    private suspend fun exportToExcelFile(file: File, items: List<Measurement>) {
        log("going to export to Excel file: $file measurements with count: ${items.size}")
        showErrorDialog(title = "Ошибка", caption = "На данный момент экспорт в Excel не реализован")
    }

    private suspend fun invalidateDataMapper() {
        val parameters = getParameters(GetEntities.Params.GetWithSpecification(Specification.QueryAll))
        if (parameters is Result.Success) {
            parameters.value.flatMap { it.items }
            updateDataMapper {
                (it as? MeasurementsDataMapper)?.let { mdm ->
                    mdm.copy(parameters = parameters.value.flatMap { it.items })
                } ?: it
            }
        }
    }


//    override fun duplicateEntities(entities: List<Measurement>) {
//        scope.launch {
//            val duplicated =
//                entities
//                    .map { msrmnt ->
//                        msrmnt.copy(
//                            id = UUID.randomUUID().toString(),
//                            results = msrmnt
//                                .results
//                                .map {
//                                    it.copy(
//
//                                    )
//                                }
//                        )
//                    }
//        }
//    }


    init {

        scope.launch {
            invalidateDataMapper()
        }

        scope.launch {
            parametersCallbacks
                .updates
                .collect {
                    invalidateDataMapper()
                }
        }
    }


    companion object {
        private const val ACTION_ADD_SINGLE = "id_add_single"
        private const val ACTION_ADD_MULTIPLE = "id_add_multiple"
        private const val ACTION_IMPORT_FROM_FILE = "id_import_from_file"
    }

}