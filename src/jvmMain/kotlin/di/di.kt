package di

import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import settings.PreferencesManager
import java.util.prefs.Preferences

val di = DI {
    //settings:
    bindSingleton<Settings> {
        val delegate: Preferences = Preferences.userRoot()
        PreferencesSettings(delegate)
    }

    bindSingleton { PreferencesManager(settings = instance()) }


    import(samplesModule)
    import(sampleTypesModule)
    import(parametersModule)
    import(operationsModule)
    import(operationTypesModule)
    import(workersModule)
    import(placesModule)
    import(measurementsModule)
    import(unitsModule)
}

