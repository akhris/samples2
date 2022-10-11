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
        FABParams(id = ACTION_ADD_PARAMETER, icon = IconResource.ImageVectorIcon(Icons.Rounded.Add), label = "")
    )

    override fun invokeFABAction(id: String, tag: Any?) {
        when (id) {
            ACTION_ADD_PARAMETER -> {
                (tag as? SampleType)?.let { st ->
                    insertNewEntity(st)
                }
            }
        }
    }

    companion object {
        private const val ACTION_ADD_PARAMETER = "id_add_parameter"
    }

}