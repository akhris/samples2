package di

import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.PreferencesSettings
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import settings.PreferencesManager
import java.util.prefs.Preferences

val preferencesModule = DI.Module("preferences") {
    bindSingleton<ObservableSettings> {
        val delegate: Preferences = Preferences.userRoot()
        PreferencesSettings(delegate)
    }

    bindSingleton { PreferencesManager(settings = instance()) }
}