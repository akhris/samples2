package di

import domain.*
import domain.application.baseUseCases.*
import kotlinx.coroutines.Dispatchers
import org.kodein.di.*
import persistence.exposed.dao.*
import persistence.repositories.BaseRepository
import ui.components.tables.IDataTableMapper
import ui.components.tables.mappers.*

/**
 * Base Entity Module to bind all UseCases and Repository
 */
inline fun <reified ENTITY : IEntity> getEntityModule(
    name: String,
    crossinline getDao: DirectDI.() -> IBaseDao<ENTITY>,
    crossinline getDataMapper: DirectDI.() -> IDataTableMapper<ENTITY>,
    crossinline additionalBindings: DI.Builder.() -> Unit = {}
): DI.Module = DI.Module(name) {
    bindSingleton { getDao() }
    bindSingleton { getDataMapper() }
    bindSingleton<BaseRepository<ENTITY>> { BaseRepository(instance()) }
    bindSingleton<IRepository<ENTITY>> { instance<BaseRepository<ENTITY>>() }
    bindSingleton<IRepositoryCallback<ENTITY>> { instance<BaseRepository<ENTITY>>() }
    bindSingleton<GetEntity<ENTITY>> { GetEntity(repo = instance(), ioDispatcher = Dispatchers.IO) }
    bindSingleton<GetItemsCount<ENTITY>> { GetItemsCount(repo = instance(), ioDispatcher = Dispatchers.IO) }
    bindSingleton<GetEntities<ENTITY>> { GetEntities(repo = instance(), ioDispatcher = Dispatchers.IO) }
    bindSingleton<RemoveEntity<ENTITY>> { RemoveEntity(repo = instance(), ioDispatcher = Dispatchers.IO) }
    bindSingleton<UpdateEntity<ENTITY>> { UpdateEntity(repo = instance(), ioDispatcher = Dispatchers.IO) }
    bindSingleton<InsertEntity<ENTITY>> { InsertEntity(repo = instance(), ioDispatcher = Dispatchers.IO) }
    additionalBindings()
}

val samplesModule =
    getEntityModule(
        name = "samples module",
        getDao = { SamplesDao() },
        getDataMapper = { SamplesDataMapper() })

val sampleTypesModule = getEntityModule(
    name = "sample types module",
    getDao = { SampleTypesDao() },
    getDataMapper = { SampleTypesDataMapper() })

val parametersModule = getEntityModule(
    name = "parameters module",
    getDao = { ParametersDao() },
    getDataMapper = { ParametersDataMapper() })
val operationsModule = getEntityModule(
    name = "operations module",
    getDao = { OperationsDao() },
    getDataMapper = { OperationsDataMapper() })
val operationTypesModule = getEntityModule(
    name = "operation types module",
    getDao = { OperationTypesDao() },
    getDataMapper = { OperationTypesDataMapper() })
val workersModule =
    getEntityModule(
        name = "workers module",
        getDao = { WorkerDao() },
        getDataMapper = { WorkersDataMapper() })
val placesModule =
    getEntityModule(
        name = "places module",
        getDao = { PlacesDao() },
        getDataMapper = { PlacesDataMapper() })

val measurementsModule =
    getEntityModule(
        name = "measurements module",
        getDao = { MeasurementsDao() },
        getDataMapper = { MeasurementsDataMapper() }
    )

val unitsModule =
    getEntityModule(
        name = "units module",
        getDao = { UnitsDao() },
        getDataMapper = { UnitsDataMapper() }
    )