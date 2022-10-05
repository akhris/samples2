package ui.screens.base_entity_screen.entityComponents

import com.arkivanov.decompose.ComponentContext
import domain.Sample
import domain.SampleType
import org.kodein.di.DI
import ui.components.IconResource
import ui.screens.base_entity_screen.EntityComponentWithFab
import ui.screens.base_entity_screen.FABParams
import utils.log

class SamplesComponent(
    di: DI,
    componentContext: ComponentContext
) : EntityComponentWithFab<Sample>(Sample::class, di, componentContext) {

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

            }
        }
    }

    override fun onEntitySelected(entity: Sample) {
        // TODO: Navigate to sample details screen here
        log("going to navigate to details screen for: $entity")
    }

    companion object {
        private const val ACTION_ADD_SINGLE = "id_add_single"
        private const val ACTION_ADD_MULTIPLE = "id_add_multiple"
    }
}