package ui.dialogs.add_sample_type_dialog

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.subscribe
import domain.SampleType
import domain.application.baseUseCases.InsertEntity
import kotlinx.coroutines.*
import org.kodein.di.DI
import org.kodein.di.instance

class AddSampleTypeDialogComponent(
    di: DI,
    componentContext: ComponentContext
) : IAddSampleTypeDialogComponent, ComponentContext by componentContext {

    private val scope =
        CoroutineScope(Dispatchers.Default + SupervisorJob())


    private val insertSampleType: InsertEntity<SampleType> by di.instance()

    override fun addSampleType(type: SampleType) {
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