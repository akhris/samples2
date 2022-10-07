package ui.screens.base_entity_screen.filter_dialog

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.reduce
import com.arkivanov.essenty.lifecycle.subscribe
import domain.*
import domain.Unit
import domain.application.Result
import domain.application.baseUseCases.GetSlice
import kotlinx.coroutines.*
import org.kodein.di.DI
import org.kodein.di.LazyDelegate
import org.kodein.di.instance
import utils.log
import kotlin.reflect.KClass

class FilterEntityFieldComponent<T : IEntity>(
    val type: KClass<out T>,
    private val di: DI,
    componentContext: ComponentContext,
    initialSpec: FilterSpec,
) : IFilterEntityFieldComponent<T>, ComponentContext by componentContext {
    private val scope =
        CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val _filterSpec: MutableValue<FilterSpec> = MutableValue(initialSpec)
    private val _slice: MutableValue<List<String>> = MutableValue(listOf())

    override val filterSpec: Value<FilterSpec> = _filterSpec
    override val slice: Value<List<String>> = _slice


    private val getSlice: GetSlice<T> by when (type) {
        Sample::class -> di.instance<GetSlice<Sample>>()
        SampleType::class -> di.instance<GetSlice<SampleType>>()
        Parameter::class -> di.instance<GetSlice<Parameter>>()
        Operation::class -> di.instance<GetSlice<Operation>>()
        OperationType::class -> di.instance<GetSlice<OperationType>>()
        Worker::class -> di.instance<GetSlice<Worker>>()
        Place::class -> di.instance<GetSlice<Place>>()
        Unit::class -> di.instance<GetSlice<Unit>>()
        Measurement::class -> di.instance<GetSlice<Measurement>>()
        else -> throw IllegalArgumentException("unsupported type: $type")
    } as LazyDelegate<GetSlice<T>>

    private suspend fun updateSlice() {
        val columnName = _filterSpec.value.columnName
        val sliceResult = getSlice(GetSlice.Params.GetSliceForColumn(columnName))
        when (sliceResult) {
            is Result.Failure -> {
                log("for column $columnName cannot get slice: ${sliceResult.throwable}")
            }

            is Result.Success -> {
                _slice.reduce {
                    sliceResult.value.map { it.toString() }
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
            updateSlice()
        }

    }


    companion object {
        inline operator fun <reified T : IEntity> invoke(
            di: DI,
            componentContext: ComponentContext,
            initialSpec: FilterSpec,
        ): FilterEntityFieldComponent<T> {
            return FilterEntityFieldComponent(
                type = T::class,
                di = di,
                componentContext = componentContext,
                initialSpec = initialSpec
            )
        }
    }
}