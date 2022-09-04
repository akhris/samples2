package di

import domain.*
import domain.application.baseUseCases.GetEntity
import org.kodein.di.*
import org.kodein.di.bindings.subTypes
import org.kodein.type.jvmType

val di = DI {
    import(samplesModule)
    import(sampleTypesModule)
    import(parametersModule)
    import(operationsModule)
    import(operationTypesModule)
    import(workersModule)
    import(placesModule)


    bind<GetEntity<out IEntity>> {
        factory { entityClass: Class<out IEntity> ->
            when (entityClass) {
                Sample::class.java -> instance<GetEntity<Sample>>()
                Worker::class.java -> instance<GetEntity<Worker>>()
                OperationType::class.java -> instance<GetEntity<OperationType>>()
                Place::class.java -> instance<GetEntity<Place>>()
                else -> throw IllegalStateException("unsupported entity class: $entityClass")
            }
        }
    }
}