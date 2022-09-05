package di

import org.kodein.di.DI

val di = DI {
    import(samplesModule)
    import(sampleTypesModule)
    import(parametersModule)
    import(operationsModule)
    import(operationTypesModule)
    import(workersModule)
    import(placesModule)
}

