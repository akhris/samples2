package ui.screens.base_entity_screen.entityComponents

import com.arkivanov.decompose.ComponentContext
import domain.Sample
import domain.SampleType
import domain.application.baseUseCases.InsertEntities
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import org.kodein.di.DI
import org.kodein.di.instance
import ui.components.IconResource
import ui.screens.base_entity_screen.EntityComponentWithFab
import ui.screens.base_entity_screen.FABParams
import utils.log

class SamplesComponent(
    di: DI,
    componentContext: ComponentContext,
    private val onSampleSelected: (Sample) -> Unit
) : EntityComponentWithFab<Sample>(Sample::class, di, componentContext) {


    private val insertEntities: InsertEntities<Sample> by di.instance()

    override fun getFabParams(): List<FABParams> = listOf(
        FABParams(
            ACTION_ADD_SINGLE,
            icon = IconResource.PainterResourceIcon("vector/plus_one_black_24dp.svg"),
            label = "Добавить запись"
        ),
        FABParams(
            ACTION_ADD_MULTIPLE,
            icon = IconResource.PainterResourceIcon("vector/playlist_add_black_24dp.svg"),
            label = "Добавить несколько"
        )
    )

    override fun invokeFABAction(id: String, tag: Any?) {
        when (id) {
            ACTION_ADD_SINGLE -> {
                (tag as? SampleType)?.let { st ->
                    insertNewEntity(st)
                }
            }

            ACTION_ADD_MULTIPLE -> {
                showAddMultipleSamplesDialog { ids ->
                    (tag as? SampleType)?.let { st ->
                        scope.launch {
                            val newEntities = ids.map { id ->
                                Sample(identifier = id, type = st)
                            }
                            insertEntities(InsertEntities.Insert(newEntities))
//                            val inserts =
//                                ids.map { async { insertNewEntitySuspend(Sample(identifier = it, type = st)) } }
//
//                            inserts.awaitAll()
                        }
//
//                        ids.forEach {
//                            insertNewEntity(Sample(identifier = it, type = st))
//                        }
                    }
                }
            }
        }
    }

    override fun onEntitySelected(entity: Sample) {
        // TODO: Navigate to sample details screen here
        log("going to navigate to details screen for: $entity")
        onSampleSelected(entity)
    }

    companion object {
        private const val ACTION_ADD_SINGLE = "id_add_single"
        private const val ACTION_ADD_MULTIPLE = "id_add_multiple"
    }
}