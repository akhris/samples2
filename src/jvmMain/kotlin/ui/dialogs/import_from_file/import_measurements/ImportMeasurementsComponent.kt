package ui.dialogs.import_from_file.import_measurements

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.subscribe
import domain.Measurement
import domain.application.Result
import kotlinx.coroutines.*
import org.kodein.di.DI
import org.kodein.di.instance
import persistence.export_import.json.application.ImportFromJSON
import persistence.export_import.json.dto.JSONMeasurement
import ui.screens.base_entity_screen.entityComponents.FileExtensions
import utils.log
import java.io.File

class ImportMeasurementsComponent(
    private val filePath: String,
    private val di: DI,
    componentContext: ComponentContext
) : IImportMeasurements, ComponentContext by componentContext {

    private val scope =
        CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val importFromJSON: ImportFromJSON<Measurement, JSONMeasurement> by di.instance()

    private val _state = MutableValue(IImportMeasurements.State())

    override val state: Value<IImportMeasurements.State> = _state


    private fun checkFile(file: File, isOpen: Boolean): Boolean {
        if (isOpen && !file.exists()) {
//            showErrorDialog(title = "Файл не существует", error = NoSuchFileException(file))
            return false
        }

        if (file.isDirectory) {
//            showErrorDialog(
//                title = "Выбранный путь является директорией",
//                error = NoSuchFileException(file, reason = "Необходимо выбрать файл")
//            )
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
//                else -> showErrorDialog(title = "Неподдерживаемый формат файла", error = FileSystemException(file))
            }
        }
    }

    private suspend fun importFromJSONFile(file: File) {
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

                log("imported json measurements: ${imported.value}")
            }
        }
    }

    private suspend fun importFromEXCELFile(file: File) {
        //make actual read from EXCEL file
    }


    init {

        componentContext
            .lifecycle
            .subscribe(onDestroy = {
                scope.coroutineContext.cancelChildren()
            })


    }


}