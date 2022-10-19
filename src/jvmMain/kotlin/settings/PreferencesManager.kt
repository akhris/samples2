package settings

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.coroutines.getStringFlow
import org.jetbrains.exposed.sql.Database
import persistence.exposed.DbSettings
import ui.dialogs.file_picker_dialog.IFilePicker
import ui.dialogs.file_picker_dialog.fileChooserDialog
import utils.log
import javax.swing.JOptionPane
import javax.swing.filechooser.FileNameExtensionFilter
import kotlin.io.path.pathString

class PreferencesManager(val settings: ObservableSettings) {

    @OptIn(ExperimentalSettingsApi::class)
    val databaseFile = settings.getStringFlow(KEY_DATABASE_FILE, getDefaultDatabaseFile())
    fun isDarkMode(): Boolean {
        return settings.getBoolean(KEY_IS_DARK_MODE, false)
    }

    fun setIsDarkMode(isDarkMode: Boolean) {
        settings.putBoolean(KEY_IS_DARK_MODE, isDarkMode)
    }

    fun setDatabaseFile(path: String) {
        log("going to set db file: $path")
        connectToDatabase(path)?.let { approvedPath ->
            log("put $approvedPath in settings")
            if (approvedPath != getDatabaseFile())
                settings.putString(KEY_DATABASE_FILE, approvedPath)
        }
    }

    fun getDatabaseFile(): String {
        return settings.getString(
            KEY_DATABASE_FILE,
            defaultValue = getDefaultDatabaseFile()
        )
    }


    fun connectToDatabase(filePath: String = getDatabaseFile()): String? {
        log("connecting to database: $filePath")
        //1. connect to file:
        val db = DbSettings.connectToDB(filePath)
        //2. check its tables.
        val checkTables = DbSettings.checkTables(db)
        if (checkTables) {
            log("tables check for $filePath is ok-> return this file")
            return filePath
        }

        //db==null, connection failed
        //yes = 0, no = 1
        val answer = JOptionPane.showConfirmDialog(
            null,
            "$filePath\n\nChoose another file?",
            "Cannot connect to database.",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.ERROR_MESSAGE
        )

        if (answer == 0) {
            val file =
                fileChooserDialog(
                    title = "Выберите файл базы данных",
                    filters = listOf(samplesExtensionFilter),
                    pickerType = IFilePicker.PickerType.SaveFile
                )
                    ?: return null
            return connectToDatabase(file.path)
        }

        return null
    }

    companion object {
        private const val KEY_IS_DARK_MODE = "is_dark_mode"
        private const val KEY_DATABASE_FILE = "database_file"
        fun getDefaultDatabaseFile(): String = AppFoldersManager.getAppPath().pathString.plus("/data.samples")
        val samplesExtensionFilter = FileNameExtensionFilter("файл базы данных образцов", "samples")
    }
}