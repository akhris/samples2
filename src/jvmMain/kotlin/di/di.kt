package di

import org.kodein.di.DI

val di = DI {

    import(samplesModule)
    import(sampleTypesModule)
    import(parametersModule)
}