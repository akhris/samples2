package ui.screens.base_entity_screen.entityComponents

import com.arkivanov.decompose.ComponentContext
import domain.*
import domain.application.Result
import domain.application.baseUseCases.GetEntities
import kotlinx.coroutines.launch
import org.kodein.di.DI
import org.kodein.di.instance
import ui.components.IconResource
import ui.components.tables.mappers.MeasurementsDataMapper
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
                        //make actual import of measurements here:
                        scope.launch {
                            importFromFile(it)
                        }
                    }
                )
            }
        }
    }


    private suspend fun importFromFile(filePath: String) {
        val file = File(filePath)
        if (!file.exists()) {
            showErrorDialog(title = "Файл не существует", error = NoSuchFileException(file))
            return
        }
        if (file.isDirectory) {
            showErrorDialog(
                title = "Выбранный путь является директорией",
                error = NoSuchFileException(file, reason = "Необходимо выбрать файл")
            )
            return
        }

        when (file.extension) {
            in FileExtensions.JSON.extensions -> importFromJSONFile(file)
            in FileExtensions.EXCEL.extensions -> importFromEXCELFile(file)
            else -> showErrorDialog(title = "Неподдерживаемый формат файла", error = FileSystemException(file))
        }
    }

    private suspend fun importFromJSONFile(file: File) {
        //make actual read from JSON file
    }

    private suspend fun importFromEXCELFile(file: File) {
        //make actual read from EXCEL file
    }

    private suspend fun invalidateDataMapper() {
        val parameters = getParameters(GetEntities.Params.GetWithSpecification(Specification.QueryAll))
        if (parameters is Result.Success) {
            (parameters.value as? EntitiesList.NotGrouped<Parameter>)?.let { params ->
                updateDataMapper {
                    (it as? MeasurementsDataMapper)?.let { mdm ->
//                        mdm.parameters = params.items
                        log("invalidating data mapper: $mdm with params: ${params.items}")
                        mdm.copy(parameters = params.items)
                    } ?: it
                }
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