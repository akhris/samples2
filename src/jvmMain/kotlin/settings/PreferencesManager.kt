package settings

import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import java.util.prefs.Preferences
import kotlin.io.path.pathString

class PreferencesManager(val settings: Settings) {

    fun isDarkMode(): Boolean {
        return settings.getBoolean(KEY_IS_DARK_MODE, false)
    }

    fun setIsDarkMode(isDarkMode: Boolean) {
        settings.putBoolean(KEY_IS_DARK_MODE, isDarkMode)
    }

    fun setDatabaseFile(path: String) {
        settings.putString(KEY_DATABASE_FILE, path)
    }

    fun getDatabaseFile(): String {
        return settings.getString(
            KEY_DATABASE_FILE,
            defaultValue = getDefaultDatabaseFile()
        )
    }


    companion object {
        private const val KEY_IS_DARK_MODE = "is_dark_mode"
        private const val KEY_DATABASE_FILE = "database_file"
        fun getDefaultDatabaseFile(): String = AppFoldersManager.getAppPath().pathString.plus("/data.db")
    }
}