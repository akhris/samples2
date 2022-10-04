package ui.screens.base_entity_screen

import androidx.compose.ui.graphics.Color
import com.arkivanov.decompose.ComponentContext
import domain.IEntity
import org.kodein.di.DI
import ui.components.IconResource
import kotlin.reflect.KClass

abstract class EntityComponentWithFab<T : IEntity>(
    type: KClass<out T>,
    di: DI,
    componentContext: ComponentContext
) : EntityComponent<T>(type, di, componentContext) {

    abstract fun getFabParams(): List<FABParams>
    abstract fun invokeFABAction(id: String, tag: Any? = null)

}

class FABParams(val id: String, val icon: IconResource, val color: Color? = null, val label: String? = null)

typealias FABAction = () -> Unit

