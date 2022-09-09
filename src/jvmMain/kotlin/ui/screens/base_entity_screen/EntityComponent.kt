package ui.screens.base_entity_screen

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.reduce
import com.arkivanov.essenty.lifecycle.subscribe
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import domain.*
import domain.application.Result
import domain.application.baseUseCases.GetEntities
import domain.application.baseUseCases.GetItemsCount
import domain.application.baseUseCases.InsertEntity
import domain.application.baseUseCases.UpdateEntity
import io.github.evanrupert.excelkt.workbook
import kotlinx.coroutines.*
import org.kodein.di.DI
import org.kodein.di.LazyDelegate
import org.kodein.di.instance
import ui.components.tables.Cell
import ui.components.tables.IDataTableMapper
import utils.DateTimeConverter
import utils.log
import java.util.*
import kotlin.reflect.KClass

class EntityComponent<T : IEntity>(
    val type: KClass<out T>,
    private val di: DI,
    componentContext: ComponentContext
) : IEntityComponent<T>,
    ComponentContext by componentContext {

    private val scope =
        CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val _spec = MutableValue<Specification>(Specification.QueryAll)
    private val _pagingSpec = MutableValue<Specification.Paginated>(Specification.Paginated(1L, 25L, null))

    override val pagingSpec: Value<Specification.Paginated> = _pagingSpec

    private val navigation = StackNavigation<Config>()


    private val _dialogStack =
        childStack(
            source = navigation,
            initialConfiguration = Config.None,
            handleBackButton = true,
            childFactory = ::createChild,
            key = "nav host stack"
        )

    override val dialogStack: Value<ChildStack<*, IEntityComponent.Dialog>> = _dialogStack


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

    private val updateEntity: UpdateEntity<T> by when (type) {
        Sample::class -> di.instance<UpdateEntity<Sample>>()
        SampleType::class -> di.instance<UpdateEntity<SampleType>>()
        Parameter::class -> di.instance<UpdateEntity<Parameter>>()
        Operation::class -> di.instance<UpdateEntity<Operation>>()
        OperationType::class -> di.instance<UpdateEntity<OperationType>>()
        Worker::class -> di.instance<UpdateEntity<Worker>>()
        Place::class -> di.instance<UpdateEntity<Place>>()
        else -> throw IllegalArgumentException("unsupported type: $type")
    } as LazyDelegate<UpdateEntity<T>>

    private val getItemsCount: GetItemsCount<T> by when (type) {
        Sample::class -> di.instance<GetItemsCount<Sample>>()
        SampleType::class -> di.instance<GetItemsCount<SampleType>>()
        Parameter::class -> di.instance<GetItemsCount<Parameter>>()
        Operation::class -> di.instance<GetItemsCount<Operation>>()
        OperationType::class -> di.instance<GetItemsCount<OperationType>>()
        Worker::class -> di.instance<GetItemsCount<Worker>>()
        Place::class -> di.instance<GetItemsCount<Place>>()
        else -> throw IllegalArgumentException("unsupported type: $type")
    } as LazyDelegate<GetItemsCount<T>>

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

    override fun insertNewEntity(sampleType: SampleType) {

        val entity = when (type) {
            Sample::class -> Sample(type = sampleType)
            SampleType::class -> SampleType()
            Parameter::class -> Parameter(sampleType = sampleType)
            Operation::class -> Operation()
            OperationType::class -> OperationType()
            Worker::class -> Worker()
            Place::class -> Place()
            else -> throw IllegalArgumentException("cannot get data table mapper!")
        } as? T

        entity?.let {
            insertNewEntity(it)
        }
    }

    override fun insertNewEntity(entity: T) {
        scope.launch {
            insertEntity(InsertEntity.Insert(entity))
        }
    }

    override fun updateEntity(entity: T) {
        scope.launch {
            updateEntity(UpdateEntity.Update(entity))
        }
    }


    override fun duplicateEntities(entities: List<T>) {
        scope.launch {
            //create duplicated copies with new id:
            val duplicated =
                entities
                    .mapNotNull {
                        when (it::class) {
                            Sample::class -> (it as? Sample)?.copy(id = UUID.randomUUID().toString())
                            SampleType::class -> (it as? SampleType)?.copy(id = UUID.randomUUID().toString())
                            Parameter::class -> (it as? Parameter)?.copy(id = UUID.randomUUID().toString())
                            Operation::class -> (it as? Operation)?.copy(id = UUID.randomUUID().toString())
                            OperationType::class -> (it as? OperationType)?.copy(id = UUID.randomUUID().toString())
                            Worker::class -> (it as? Worker)?.copy(id = UUID.randomUUID().toString())
                            Place::class -> (it as? Place)?.copy(id = UUID.randomUUID().toString())
                            else -> throw IllegalArgumentException("cannot get data table mapper!")
                        }
                    }

            //insert duplicates:
            duplicated
                .forEach {
                    insertEntity(InsertEntity.Insert(it))
                }
        }
    }

    override fun setQuerySpec(spec: Specification) {
        _spec.reduce {
            spec
        }
        scope.launch {
            invalidateEntities()
        }
    }

    override fun setPagingSpec(spec: Specification.Paginated) {
        _pagingSpec.reduce {
            spec
        }
        scope.launch {
            invalidateEntities()
        }
    }

    override fun resetQuerySpec(spec: Specification) {
        setQuerySpec(Specification.QueryAll)
    }

    override fun saveRowsToExcel(entities: List<T>) {
        scope.launch {
            workbook {
                sheet {

                    row {
                        //header:
                        dataMapper.columns.forEach {
                            cell(content = it.title)
                        }
                    }
                    entities.forEach { entity ->
                        row {
                            dataMapper.columns.forEach { column ->
                                val cell = dataMapper.getCell(entity, column)
                                cell(
                                    when (cell) {
                                        is Cell.DateTimeCell -> cell.value?.let { DateTimeConverter.dateTimeToString(it) }
                                            ?: ""

                                        is Cell.EditTextCell -> cell.value
                                        is Cell.EntityCell -> cell.entity?.toString() ?: ""
                                    }
                                )
                            }
                        }
                    }

                }
            }.write("test.xlsx")
        }
    }


    private suspend fun invalidateEntities() {
        //get all samples
        val entities = getEntities(GetEntities.Params.GetWithSpecification(_spec.value, _pagingSpec.value))

        when (entities) {
            is Result.Success -> {
                _state.reduce {
                    it.copy(
                        entities = entities.value
                    )
                }
            }

            is Result.Failure -> {
                log(entities.throwable)
                log(entities.throwable.stackTraceToString())
            }
        }
    }

    private fun createChild(config: Config, componentContext: ComponentContext): IEntityComponent.Dialog {
        return when (config) {
            is Config.EntityPickerDialog -> IEntityComponent.Dialog.EntityPicker(
                EntityComponent(
                    type = config.entityClass,
                    di = di,
                    componentContext = componentContext
                ),
                initialSelection = config.entity?.id,
                onSelectionChanged = config.onSelectionChanged,
                columnName = config.columnName
            )

            Config.None -> IEntityComponent.Dialog.None
        }
    }

    override fun dismissDialog() {
        navigation.replaceCurrent(Config.None)
    }

    override fun showEntityPickerDialog(
        entity: IEntity?,
        entityClass: KClass<out IEntity>,
        onSelectionChanged: (IEntity?) -> Unit,
        columnName: String
    ) {
        navigation.replaceCurrent(Config.EntityPickerDialog(entity, entityClass, onSelectionChanged, columnName))
    }

    private suspend fun invalidateItemsCount() {
        val itemsCount = getItemsCount(GetItemsCount.Params.GetBySpecifications(_spec.value))
        when (itemsCount) {
            is Result.Failure -> {
                log("could not get items count: ${itemsCount.throwable.localizedMessage}")
            }

            is Result.Success -> {
                log("got items count: ${itemsCount.value}")
                _pagingSpec.reduce {
                    it.copy(totalItems = itemsCount.value)
                }
            }
        }

    }

    init {

        lifecycle.subscribe(onDestroy = {
            scope.coroutineContext.cancelChildren()
        })

        scope.launch {
            //invalidate pagination params
            invalidateItemsCount()
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

    @Parcelize
    private sealed class Config : Parcelable {
        @Parcelize
        object None : Config()

        @Parcelize
        class EntityPickerDialog(
            val entity: IEntity?,
            val entityClass: KClass<out IEntity>,
            val onSelectionChanged: (IEntity?) -> Unit,
            val columnName: String
        ) : Config()
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