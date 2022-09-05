package ui.screens.base_entity_screen

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.reduce
import com.arkivanov.essenty.lifecycle.subscribe
import domain.*
import domain.application.Result
import domain.application.baseUseCases.GetEntities
import domain.application.baseUseCases.InsertEntity
import domain.application.baseUseCases.UpdateEntity
import kotlinx.coroutines.*
import org.kodein.di.DI
import org.kodein.di.LazyDelegate
import org.kodein.di.instance
import ui.components.tables.IDataTableMapper
import utils.log
import kotlin.reflect.KClass

class EntityComponent<T : IEntity>(
    val type: KClass<out T>,
    private val di: DI,
    componentContext: ComponentContext
) : IEntityComponent<T>,
    ComponentContext by componentContext {

    private val scope =
        CoroutineScope(Dispatchers.Default + SupervisorJob())


    private val getEntities: GetEntities<T> by when (type) {
        Sample::class -> di.instance<GetEntities<Sample>>()
        SampleType::class -> di.instance<GetEntities<SampleType>>()
        Parameter::class -> di.instance<GetEntities<Parameter>>()
        Operation::class -> di.instance<GetEntities<Operation>>()
        OperationType::class -> di.instance<GetEntities<OperationType>>()
        Worker::class -> di.instance<GetEntities<Worker>>()
        Place::class -> di.instance<GetEntities<Place>>()
        else -> throw IllegalArgumentException("unsupported type: $type")
    } as LazyDelegate<GetEntities<T>>


    private val insertEntity: InsertEntity<T> by when (type) {
        Sample::class -> di.instance<InsertEntity<Sample>>()
        SampleType::class -> di.instance<InsertEntity<SampleType>>()
        Parameter::class -> di.instance<InsertEntity<Parameter>>()
        Operation::class -> di.instance<InsertEntity<Operation>>()
        OperationType::class -> di.instance<InsertEntity<OperationType>>()
        Worker::class -> di.instance<InsertEntity<Worker>>()
        Place::class -> di.instance<InsertEntity<Place>>()
        else -> throw IllegalArgumentException("unsupported type: $type")
    } as LazyDelegate<InsertEntity<T>>

    private val updatEntity: UpdateEntity<T> by when (type) {
        Sample::class -> di.instance<UpdateEntity<Sample>>()
        SampleType::class -> di.instance<UpdateEntity<SampleType>>()
        Parameter::class -> di.instance<UpdateEntity<Parameter>>()
        Operation::class -> di.instance<UpdateEntity<Operation>>()
        OperationType::class -> di.instance<UpdateEntity<OperationType>>()
        Worker::class -> di.instance<UpdateEntity<Worker>>()
        Place::class -> di.instance<UpdateEntity<Place>>()
        else -> throw IllegalArgumentException("unsupported type: $type")
    } as LazyDelegate<UpdateEntity<T>>

    private val repositoryCallbacks: IRepositoryCallback<T> by when (type) {
        Sample::class -> di.instance<IRepositoryCallback<Sample>>()
        SampleType::class -> di.instance<IRepositoryCallback<SampleType>>()
        Parameter::class -> di.instance<IRepositoryCallback<Parameter>>()
        Operation::class -> di.instance<IRepositoryCallback<Operation>>()
        OperationType::class -> di.instance<IRepositoryCallback<OperationType>>()
        Worker::class -> di.instance<IRepositoryCallback<Worker>>()
        Place::class -> di.instance<IRepositoryCallback<Place>>()
        else -> throw IllegalArgumentException("unsupported type: $type")
    } as LazyDelegate<IRepositoryCallback<T>>

    override val dataMapper: IDataTableMapper<T> by when (type) {
        Sample::class -> di.di.instance<IDataTableMapper<Sample>>()
        SampleType::class -> di.di.instance<IDataTableMapper<SampleType>>()
        Parameter::class -> di.di.instance<IDataTableMapper<Parameter>>()
        Operation::class -> di.di.instance<IDataTableMapper<Operation>>()
        OperationType::class -> di.di.instance<IDataTableMapper<OperationType>>()
        Worker::class -> di.di.instance<IDataTableMapper<Worker>>()
        Place::class -> di.di.instance<IDataTableMapper<Place>>()
        else -> throw IllegalArgumentException("cannot get data table mapper!")
    } as LazyDelegate<IDataTableMapper<T>>

    private val _state = MutableValue(IEntityComponent.State<T>())
    override val state: Value<IEntityComponent.State<T>> = _state

    override fun insertNewEntity(entity: T) {
        scope.launch {
            insertEntity(InsertEntity.Insert(entity))
        }
    }

    override fun updateEntity(entity: T) {
        scope.launch {
            updatEntity(UpdateEntity.Update(entity))
        }
    }


    private suspend fun invalidateEntities() {
        //get all samples
        log("getEntities: $getEntities")
        val entities = getEntities(GetEntities.Params.GetWithSpecification(Specification.QueryAll))

        when (entities) {
            is Result.Success -> {
                _state.reduce {
                    it.copy(
                        entities = when (val list = entities.value) {
                            is EntitiesList.Grouped -> listOf()
                            is EntitiesList.NotGrouped -> list.items
                        }
                    )
                }
            }

            is Result.Failure -> {
                log(entities.throwable)
                log(entities.throwable.stackTraceToString())
            }
        }
    }

    init {
        log("EntityComponent for $type")

        lifecycle.subscribe(onDestroy = {
            scope.coroutineContext.cancelChildren()
        })

        scope.launch {
            invalidateEntities()
        }

        //subscribe to repository callbacks:
        scope.launch {
            repositoryCallbacks.updates.collect {
                when (it) {
                    is RepoResult.ItemInserted,
                    is RepoResult.ItemRemoved,
                    is RepoResult.ItemUpdated -> {
                        invalidateEntities()
                    }
                }
            }
        }

    }


    companion object {
        inline operator fun <reified T : IEntity> invoke(
            di: DI,
            componentContext: ComponentContext
        ): EntityComponent<T> {

            return EntityComponent(type = T::class, di = di, componentContext = componentContext)
        }
    }
}