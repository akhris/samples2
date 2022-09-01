package di

import domain.*
import domain.application.baseUseCases.*
import kotlinx.coroutines.Dispatchers
import org.kodein.di.DI
import org.kodein.di.DirectDI
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import persistence.dao.OperationsDao
import persistence.dao.ParametersDao
import persistence.dao.SampleTypesDao
import persistence.dao.SamplesDao
import persistence.repositories.BaseRepository

/**
 * Base Entity Module to bind all UseCases and Repository
 */
inline fun <reified ENTITY : IEntity> getEntityModule(
    name: String,
    crossinline getDao: DirectDI.() -> IBaseDao<ENTITY>,
    crossinline additionalBindings: DI.Builder.() -> Unit = {}
): DI.Module = DI.Module(name) {
    bindSingleton<IBaseDao<ENTITY>> { getDao() }
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

val samplesModule = getEntityModule<Sample>(name = "samples module", getDao = { SamplesDao() })
val sampleTypesModule = getEntityModule<SampleType>(name = "sample types module", getDao = { SampleTypesDao() })
val parametersModule = getEntityModule<Parameter>(name = "parameters module", getDao = { ParametersDao() })
val operationsModule = getEntityModule<Operation>(name = "operations module", getDao = { OperationsDao() })
