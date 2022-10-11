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
import domain.application.baseUseCases.*
import io.github.evanrupert.excelkt.workbook
import kotlinx.coroutines.*
import org.kodein.di.DI
import org.kodein.di.LazyDelegate
import org.kodein.di.instance
import persistence.Column
import ui.components.tables.Cell
import ui.components.tables.IDataTableMapper
import ui.screens.base_entity_screen.filter_dialog.FilterEntityFieldComponent
import ui.dialogs.error_dialog.ErrorDialogComponent
import utils.DateTimeConverter
import utils.log
import utils.replaceOrAdd
import java.util.*
import kotlin.Unit
import kotlin.reflect.KClass

open class EntityComponent<T : IEntity>(
    val type: KClass<out T>,
    private val di: DI,
    componentContext: ComponentContext,
    initialFilterSpec: Specification.Filtered = Specification.Filtered(listOf(), true)
) : IEntityComponent<T>,
    ComponentContext by componentContext {

    protected val scope =
        CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val _spec = MutableValue<Specification>(Specification.QueryAll)
    private val _pagingSpec = MutableValue<Specification.Paginated>(Specification.Paginated(1L, 25L, null))
    private val _filterSpec = MutableValue<Specification.Filtered>(initialFilterSpec)
    private val _sampleTypeFilterSpec = MutableValue(
        Specification.Filtered(
            listOf(
                FilterSpec.Values(
                    listOf(),
                    columnName = Column.SampleType.columnName
                )
            )
        )
    )

    override val pagingSpec: Value<Specification.Paginated> = _pagingSpec

    override val filterSpec: Value<Specification.Filtered> = _filterSpec

    private val dialogNav = StackNavigation<DialogConfig>()


    private val _dialogStack =
        childStack(
            source = dialogNav,
            initialConfiguration = DialogConfig.None,
//            handleBackButton = true,
            childFactory = ::createChild,
            key = "entity picker dialog stack"
        )

    override val dialogStack: Value<ChildStack<*, IEntityComponent.Dialog>> = _dialogStack

    override val isReorderable: Boolean = when (type) {
        Parameter::class -> true
        else -> false
    }

    private val getEntities: GetEntities<T> by when (type) {
        Sample::class -> di.instance<GetEntities<Sample>>()
        SampleType::class -> di.instance<GetEntities<SampleType>>()
        Parameter::class -> di.instance<GetEntities<Parameter>>()
        Operation::class -> di.instance<GetEntities<Operation>>()
        OperationType::class -> di.instance<GetEntities<OperationType>>()
        Worker::class -> di.instance<GetEntities<Worker>>()
        Place::class -> di.instance<GetEntities<Place>>()
        domain.Unit::class -> di.instance<GetEntities<domain.Unit>>()
        Measurement::class -> di.instance<GetEntities<Measurement>>()
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
        domain.Unit::class -> di.instance<InsertEntity<domain.Unit>>()
        Measurement::class -> di.instance<InsertEntity<Measurement>>()
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
        domain.Unit::class -> di.instance<UpdateEntity<domain.Unit>>()
        Measurement::class -> di.instance<UpdateEntity<Measurement>>()
        else -> throw IllegalArgumentException("unsupported type: $type")
    } as LazyDelegate<UpdateEntity<T>>

    private val updateEntities: UpdateEntities<T> by when (type) {
        Sample::class -> di.instance<UpdateEntities<Sample>>()
        SampleType::class -> di.instance<UpdateEntities<SampleType>>()
        Parameter::class -> di.instance<UpdateEntities<Parameter>>()
        Operation::class -> di.instance<UpdateEntities<Operation>>()
        OperationType::class -> di.instance<UpdateEntities<OperationType>>()
        Worker::class -> di.instance<UpdateEntities<Worker>>()
        Place::class -> di.instance<UpdateEntities<Place>>()
        domain.Unit::class -> di.instance<UpdateEntities<domain.Unit>>()
        Measurement::class -> di.instance<UpdateEntities<Measurement>>()
        else -> throw IllegalArgumentException("unsupported type: $type")
    } as LazyDelegate<UpdateEntities<T>>

    private val getItemsCount: GetItemsCount<T> by when (type) {
        Sample::class -> di.instance<GetItemsCount<Sample>>()
        SampleType::class -> di.instance<GetItemsCount<SampleType>>()
        Parameter::class -> di.instance<GetItemsCount<Parameter>>()
        Operation::class -> di.instance<GetItemsCount<Operation>>()
        OperationType::class -> di.instance<GetItemsCount<OperationType>>()
        Worker::class -> di.instance<GetItemsCount<Worker>>()
        Place::class -> di.instance<GetItemsCount<Place>>()
        domain.Unit::class -> di.instance<GetItemsCount<domain.Unit>>()
        Measurement::class -> di.instance<GetItemsCount<Measurement>>()
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
        domain.Unit::class -> di.instance<IRepositoryCallback<domain.Unit>>()
        Measurement::class -> di.instance<IRepositoryCallback<Measurement>>()
        else -> throw IllegalArgumentException("unsupported type: $type")
    } as LazyDelegate<IRepositoryCallback<T>>

    private val _dataMapper: IDataTableMapper<T> by when (type) {
        Sample::class -> di.instance<IDataTableMapper<Sample>>()
        SampleType::class -> di.instance<IDataTableMapper<SampleType>>()
        Parameter::class -> di.instance<IDataTableMapper<Parameter>>()
        Operation::class -> di.instance<IDataTableMapper<Operation>>()
        OperationType::class -> di.instance<IDataTableMapper<OperationType>>()
        Worker::class -> di.instance<IDataTableMapper<Worker>>()
        Place::class -> di.instance<IDataTableMapper<Place>>()
        domain.Unit::class -> di.instance<IDataTableMapper<domain.Unit>>()
        Measurement::class -> di.instance<IDataTableMapper<Measurement>>()
        else -> throw IllegalArgumentException("cannot get data table mapper!")
    } as LazyDelegate<IDataTableMapper<T>>


    private val _mutableDataMapper = MutableValue<IDataTableMapper<T>>(_dataMapper)

    override val dataMapper: Value<IDataTableMapper<T>> = _mutableDataMapper

    private val _state = MutableValue(IEntityComponent.State<T>())
    override val state: Value<IEntityComponent.State<T>> = _state

    protected fun updateDataMapper(reducer: (IDataTableMapper<T>) -> IDataTableMapper<T>) {
        _mutableDataMapper.reduce(reducer)
    }

    override fun onEntitySelected(entity: T) {
        //do nothing
    }

    override fun setSampleType(sampleType: SampleType) {
        _sampleTypeFilterSpec.reduce {
            it.copy(
                filters = listOf(
                    FilterSpec.Values(
                        filteredValues = listOf(sampleType.id),
                        columnName = Column.SampleType.columnName
                    )
                )
            )
        }
        scope.launch {
            invalidateItemsCount()
            invalidateEntities()
        }
    }

    override fun insertNewEntity(sampleType: SampleType) {

        val entity = when (type) {
            Sample::class -> Sample(type = sampleType)
            SampleType::class -> SampleType()
            Parameter::class -> Parameter(sampleType = sampleType)
            Operation::class -> Operation(sampleType = sampleType)
            OperationType::class -> OperationType()
            Worker::class -> Worker()
            Place::class -> Place()
            domain.Unit::class -> Unit()
            Measurement::class -> Measurement()
            else -> throw IllegalArgumentException("cannot create new entity of type: $type!")
        } as? T

        entity?.let {
            insertNewEntity(it)
        }
    }

    override fun insertNewEntity(entity: T) {
        scope.launch {
            val result = insertEntity(InsertEntity.Insert(entity))
            log(result)
        }
    }

    override fun updateEntity(entity: T) {
        scope.launch {
            when (val result = updateEntity(UpdateEntity.Update(entity))) {
                is Result.Failure -> {
                    dialogNav.replaceCurrent(
                        DialogConfig.RepoErrorDialog(
                            title = "Ошибка при обновлении объекта: $entity",
                            caption = "тип данных: ${type.simpleName}",
                            error = result.throwable
                        )
                    )
                }

                is Result.Success -> {

                }
            }
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
                            Measurement::class -> (it as? Measurement)?.copy(id = UUID.randomUUID().toString())
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
            invalidateItemsCount()
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


    override fun addFilter(filterSpec: FilterSpec) {
        _filterSpec.reduce {
            it.copy(filters = it.filters.replaceOrAdd(filterSpec) {
                it.columnName == filterSpec.columnName
            })
        }
        scope.launch {
            invalidateItemsCount()
            invalidateEntities()
        }
    }

    override fun removeFilter(filterSpec: FilterSpec) {
        _filterSpec.reduce { spec: Specification.Filtered ->
            spec.copy(filters = spec.filters.filter { it.columnName != filterSpec.columnName })
        }
        scope.launch {
            invalidateItemsCount()
            invalidateEntities()
        }
    }

    override fun saveRowsToExcel(entities: List<T>) {
        scope.launch {
            workbook {
                sheet {

                    row {
                        //header:
                        _dataMapper.columns.forEach {
                            cell(content = it.title)
                        }
                    }
                    entities.forEach { entity ->
                        row {
                            _dataMapper.columns.forEach { column ->
                                val cell = _dataMapper.getCell(entity, column)
                                cell(
                                    when (cell) {
                                        is Cell.DateTimeCell -> cell.value?.let { DateTimeConverter.dateTimeToString(it) }
                                            ?: ""

                                        is Cell.EditTextCell -> cell.value
                                        is Cell.EntityCell -> cell.entity?.toString() ?: ""
                                        is Cell.BooleanCell -> TODO()
                                        is Cell.ListCell -> TODO()
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
        val entities =
            getEntities(
                GetEntities.Params.GetWithSpecification(
                    _spec.value,
                    _pagingSpec.value,
                    _filterSpec.value,
                    _sampleTypeFilterSpec.value
                )
            )

        when (entities) {
            is Result.Success -> {
                _state.reduce {
                    it.copy(
                        entities = entities.value
                    )
                }
            }

            is Result.Failure -> {
                dialogNav.replaceCurrent(
                    DialogConfig.RepoErrorDialog(
                        title = "Ошибка при обновлении записей",
                        caption = "тип данных: ${type.simpleName}",
                        error = entities.throwable
                    )
                )
            }
        }
    }

    private fun createChild(dialogConfig: DialogConfig, componentContext: ComponentContext): IEntityComponent.Dialog {
        return when (dialogConfig) {
            is DialogConfig.EntityPickerDialog -> IEntityComponent.Dialog.EntityPicker(
                EntityComponent(
                    type = dialogConfig.entityClass,
                    di = di,
                    componentContext = componentContext
                ),
                initialSelection = dialogConfig.entity?.id,
                onSelectionChanged = dialogConfig.onSelectionChanged,
                columnName = dialogConfig.columnName
            )

            is DialogConfig.FieldFilterDialog -> IEntityComponent.Dialog.FieldFilter(
                component = FilterEntityFieldComponent(
                    type = type,
                    di = di,
                    componentContext = componentContext,
                    initialSpec = dialogConfig.initialSpec,
                    onSpecChanged = {
                        addFilter(it)
                    }
                )
            )

            is DialogConfig.RepoErrorDialog -> IEntityComponent.Dialog.ErrorDialog(
                component = ErrorDialogComponent(
                    title = dialogConfig.title,
                    caption = dialogConfig.caption,
                    error = dialogConfig.error,
                    di = di,
                    componentContext = componentContext
                )
            )

            DialogConfig.None -> IEntityComponent.Dialog.None
        }
    }

    override fun dismissDialog() {
        dialogNav.replaceCurrent(DialogConfig.None)
    }

    override fun showEntityPickerDialog(
        entity: IEntity?,
        entityClass: KClass<out IEntity>,
        onSelectionChanged: (IEntity?) -> Unit,
        columnName: String
    ) {
        dialogNav.replaceCurrent(DialogConfig.EntityPickerDialog(entity, entityClass, onSelectionChanged, columnName))
    }

    override fun showFilterDialog(columnFilters: FilterSpec) {
        dialogNav.replaceCurrent(DialogConfig.FieldFilterDialog(columnFilters))
    }

    private suspend fun invalidateItemsCount() {
        val itemsCount = getItemsCount(
            GetItemsCount.Params.GetBySpecifications(
                _spec.value,
                _filterSpec.value,
                _sampleTypeFilterSpec.value
            )
        )
        when (itemsCount) {
            is Result.Failure -> {
                dialogNav.replaceCurrent(
                    DialogConfig.RepoErrorDialog(
                        title = "Ошибка при обновлении количества записей",
                        caption = "тип данных: ${type.simpleName}",
                        error = itemsCount.throwable
                    )
                )
            }

            is Result.Success -> {
                _pagingSpec.reduce {
                    it.copy(totalItems = itemsCount.value)
                }
            }
        }

    }

    init {
        componentContext
            .lifecycle
            .subscribe(onDestroy = {
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
                        invalidateItemsCount()
                        invalidateEntities()
                    }
                }
            }
        }

    }

    @Parcelize
    private sealed class DialogConfig : Parcelable {
        @Parcelize
        object None : DialogConfig()

        @Parcelize
        class EntityPickerDialog(
            val entity: IEntity?,
            val entityClass: KClass<out IEntity>,
            val onSelectionChanged: (IEntity?) -> Unit,
            val columnName: String
        ) : DialogConfig()

        class FieldFilterDialog(val initialSpec: FilterSpec) : DialogConfig()

        class RepoErrorDialog(val title: String = "", val caption: String = "", val error: Throwable? = null) :
            DialogConfig()
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