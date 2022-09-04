package di

import domain.*
import domain.application.baseUseCases.*
import kotlinx.coroutines.Dispatchers
import org.kodein.di.DI
import org.kodein.di.DirectDI
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import persistence.dao.*
import persistence.repositories.BaseRepository

/**
 * Base Entity Module to bind all UseCases and Repository
 */
inline fun <reified ENTITY : IEntity> getEntityModule(
    name: String,
    crossinline getDao: DirectDI.() -> IBaseDao<ENTITY>,
    crossinline additionalBindings: DI.Builder.() -> Unit = {}
): DI.Module = DI.Module(name) {
    bindSingleton { getDao() }
    bindSingleton<BaseRepository<ENTITY>> { BaseRepository(instance()) }
    bindSingleton<IRepository<ENTITY>> { instance<BaseRepository<ENTITY>>() }
    bindSingleton<IRepositoryCallback<ENTITY>> { instance<BaseRepository<ENTITY>>() }
    bindSingleton<GetEntity<ENTITY>> { GetEntity(repo = instance(), ioDispatcher = Dispatchers.IO) }
    bindSingleton<GetEntities<ENTITY>> { GetEntities(repo = instance(), ioDispatcher = Dispatchers.IO) }
    bindSingleton<RemoveEntity<ENTITY>> { RemoveEntity(repo = instance(), ioDispatcher = Dispatchers.IO) }
    bindSingleton<UpdateEntity<ENTITY>> { UpdateEntity(repo = instance(), ioDispatcher = Dispatchers.IO) }
    bindSingleton<InsertEntity<ENTITY>> { InsertEntity(repo = instance(), ioDispatcher = Dispatchers.IO) }
    additionalBindings()
}

val samplesModule = getEntityModule(name = "samples module", getDao = { SamplesDao() })
val sampleTypesModule = getEntityModule(name = "sample types module", getDao = { SampleTypesDao() })
val parametersModule = getEntityModule(name = "parameters module", getDao = { ParametersDao() })
val operationsModule = getEntityModule(name = "operations module", getDao = { OperationsDao() })
val operationTypesModule = getEntityModule(name = "operation types module", getDao = { OperationTypesDao() })
val workersModule = getEntityModule(name = "workers module", getDao = { WorkerDao() })
val placesModule = getEntityModule(name = "places module", getDao = { PlacesDao() })
