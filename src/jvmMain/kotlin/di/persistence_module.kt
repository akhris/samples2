package di

import kotlinx.serialization.json.Json
import org.kodein.di.DI
import org.kodein.di.bindSingleton

val persistenceModule = DI.Module("persistence") {
    bindSingleton {
        Json {
            prettyPrint = true
            ignoreUnknownKeys = true
        }
    }
}