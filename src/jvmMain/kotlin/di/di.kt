package di

import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import settings.PreferencesManager
import java.util.prefs.Preferences

val di = DI {
    import(preferencesModule)

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

