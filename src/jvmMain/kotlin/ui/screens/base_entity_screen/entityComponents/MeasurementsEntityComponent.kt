package ui.screens.base_entity_screen.entityComponents

import com.arkivanov.decompose.ComponentContext
import domain.*
import domain.application.Result
import domain.application.baseUseCases.GetEntities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.kodein.di.DI
import org.kodein.di.instance
import ui.components.IconResource
import ui.components.tables.mappers.MeasurementsDataMapper
import ui.dialogs.file_picker_dialog.IFilePicker
import ui.dialogs.list_picker_dialog.ListPickerItem
import ui.dialogs.list_picker_dialog.ListPickerMode
import ui.screens.base_entity_screen.EntityComponentWithFab
import ui.screens.base_entity_screen.FABParams
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

    private sealed class FileExtensions(val descr: String, vararg val extensions: String) {
        object JSON : FileExtensions(descr = "JSON текстовые документы", "txt", "json")
        object EXCEL : FileExtensions(descr = "Протоколы измерений в формате EXCEL", "xls", "xlsx")
    }

    private fun FileExtensions.toFileNameExtensionsFilter(): FileNameExtensionFilter =
        FileNameExtensionFilter(descr, *extensions)


    override fun shareEntities(entities: List<Measurement>) {

        val saveAsExcelID = "save_as_excel"
        val saveAsJSONID = "save_as_json"


//        // show dialog to pick sharing format:
//        showItemsPickerDialog(
//            title = "Отправить результаты измерений.", items = listOf(
//                ListPickerItem(
//                    id = saveAsJSONID,
//                    title = "Сохранить в текстовом формате.",
//                    caption = "В виде строки JSON"
//                ),
//                ListPickerItem(
//                    id = saveAsExcelID,
//                    title = "Сохранить в формате Excel.",
//                    caption = "В виде таблицы MS Excel"
//                )
//            ),
//            mode = ListPickerMode.SingleSelect(initialSelection = saveAsJSONID, onItemSelected = { selectedID ->
//                log("selected item: $selectedID")
//
//
//
//            })
//        )


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
                    onFileSelectedCallback = ::importFromFile,
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


    private fun importFromFile(file: File) {
        if (!checkFile(file, isOpen = true)) return
        scope.launch {
            when (file.extension) {
                in FileExtensions.JSON.extensions -> importFromJSONFile(file)
                in FileExtensions.EXCEL.extensions -> importFromEXCELFile(file)
                else -> showErrorDialog(title = "Неподдерживаемый формат файла", error = FileSystemException(file))
            }
        }
    }

    private suspend fun importFromJSONFile(file: File) {
        //make actual read from JSON file

    }

    private suspend fun importFromEXCELFile(file: File) {
        //make actual read from EXCEL file
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
        withContext(Dispatchers.IO) {
            val format = Json { prettyPrint = true }
            val json = format.encodeToString(items)
            file.writeText(text = json)
            showPrompt(
                title = "Результаты экспорта",
                message = "${items.size} результатов измерений записаны в файл $file\nОткрыть файл?",
                {}
            )
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