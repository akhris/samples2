package ui.dialogs.edit_sample_type_dialog

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.operator.map
import com.arkivanov.decompose.value.reduce
import com.arkivanov.essenty.lifecycle.subscribe
import domain.SampleType
import domain.application.baseUseCases.InsertEntity
import domain.application.baseUseCases.RemoveEntity
import domain.application.baseUseCases.UpdateEntity
import kotlinx.coroutines.*
import org.kodein.di.DI
import org.kodein.di.instance

class EditSampleTypeDialogComponent(
    di: DI,
    componentContext: ComponentContext,
    private val initialSampleType: SampleType? = null
) : IEditSampleTypeDialogComponent, ComponentContext by componentContext {

    private val _sampleType = MutableValue(initialSampleType ?: SampleType())

    override val sampleType: Value<SampleType> = _sampleType

    override val isChanged: Value<Boolean> = sampleType.map { it != initialSampleType }

    override val dialogType: Value<IEditSampleTypeDialogComponent.DialogType> = MutableValue(
        if (initialSampleType == null) {
            IEditSampleTypeDialogComponent.DialogType.Add
        } else IEditSampleTypeDialogComponent.DialogType.Edit
    )

    private val scope =
        CoroutineScope(Dispatchers.Default + SupervisorJob())


    private val insertSampleType: InsertEntity<SampleType> by di.instance()
    private val updateSampleType: UpdateEntity<SampleType> by di.instance()
    private val removeSampleType: RemoveEntity<SampleType> by di.instance()

    override fun updateSampleTypeInCache(type: SampleType) {
        _sampleType.reduce {
            type
        }
    }

    override fun removeSampleType() {
        initialSampleType?.let {
            scope.launch {
                removeSampleType(RemoveEntity.Remove(it))
            }
        }
    }


    override fun updateSampleTypeInStorage(type: SampleType) {
        scope.launch {
            updateSampleType(UpdateEntity.Update(type))
        }
    }

    override fun insertSampleType(type: SampleType) {
        scope.launch {
            insertSampleType(InsertEntity.Insert(type))
        }
    }


    init {
        lifecycle.subscribe(
            onDestroy = {
                scope.coroutineContext.cancelChildren()
            })
    }

}