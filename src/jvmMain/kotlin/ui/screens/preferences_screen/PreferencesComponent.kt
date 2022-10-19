package ui.screens.preferences_screen

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.reduce
import org.kodein.di.DI
import org.kodein.di.instance
import settings.PreferencesManager
import utils.replace

class PreferencesComponent(
    private val di: DI,
    componentContext: ComponentContext
) : IPreferencesComponent, ComponentContext by componentContext {

    private val prefsManager: PreferencesManager by di.instance()

    private val _prefs = MutableValue<List<PreferenceItem>>(listOf())

    override val prefs: Value<List<PreferenceItem>> = _prefs

    override fun updatePref(pref: PreferenceItem) {
        //update state:
        _prefs.reduce {
            it.replace(pref) { oldItem ->
                oldItem.id == pref.id
            }
        }

        //update in manager:
        when (pref.id) {
            DB_FILE_PREFERENCE_ID -> (pref as? PreferenceItem.FilePreference)?.let {
                prefsManager.setDatabaseFile(it.path)
            }
        }
    }

    private fun invalidatePrefs() {
        val dbFilePref =
            PreferenceItem.FilePreference(
                id = DB_FILE_PREFERENCE_ID,
                name = "Файл базы данных",
                description = "База данных типа SQLite",
                path = prefsManager.getDatabaseFile()
            )

        _prefs.reduce {
            listOf(dbFilePref)
        }
    }

    init {
        invalidatePrefs()
    }

    companion object {
        private const val DB_FILE_PREFERENCE_ID = "db_file_preference"
    }

}