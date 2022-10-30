package ui.screens.base_entity_screen.entityComponents

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import com.arkivanov.decompose.ComponentContext
import domain.Parameter
import domain.SampleType
import org.kodein.di.DI
import ui.components.IconResource
import ui.screens.base_entity_screen.EntityComponentWithFab
import ui.screens.base_entity_screen.FABParams

class ParametersComponent(
    di: DI,
    componentContext: ComponentContext
) : EntityComponentWithFab<Parameter>(Parameter::class, di, componentContext) {

    override fun getFabParams(): List<FABParams> = listOf(
        FABParams(
            id = ACTION_ADD_PARAMETER,
            icon = IconResource.ImageVectorIcon(Icons.Rounded.Add),
            label = "Добавить параметр"
        ),
        FABParams(
            id = ACTION_ADD_NORM_CONDITION,
            icon = IconResource.PainterResourceIcon("vector/compare_arrows_black_24dp.svg"),
            label = "Добавить столбец норм"
        )
    )

    override fun invokeFABAction(id: String, tag: Any?) {
        when (id) {
            ACTION_ADD_PARAMETER -> {
                (tag as? SampleType)?.let { st ->
                    insertNewEntity(st)
                }
            }

            ACTION_ADD_NORM_CONDITION -> {
                showPrompt(title = "Добавить столбец норм", "Запросить condition", {})
            }
        }
    }

    companion object {
        private const val ACTION_ADD_PARAMETER = "id_add_parameter"
        private const val ACTION_ADD_NORM_CONDITION = "id_add_norm_condition"
    }

}