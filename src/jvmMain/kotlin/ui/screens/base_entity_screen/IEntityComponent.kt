package ui.screens.base_entity_screen

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import domain.IEntity
import ui.components.tables.IDataTableMapper
import kotlin.reflect.KClass

interface IEntityComponent<T : IEntity> {
    val state: Value<State<T>>

    fun insertNewEntity(entity: T)
    fun updateEntity(entity: T)

    val dataMapper: IDataTableMapper<T>

    data class State<E : IEntity>(
        val entities: List<E> = listOf()
    )

    /**
     * Exposes Router State
     */
    val dialogStack: Value<ChildStack<*, Dialog>>

    fun dismissDialog()

    fun showEntityPickerDialog(
        entity: IEntity?,
        entityClass: KClass<out IEntity>,
        onSelectionChanged: (IEntity?) -> Unit
    )

    sealed class Dialog {
        object None : Dialog()
        class EntityPicker<T : IEntity>(
            val component: IEntityComponent<T>,
            val initialSelection: String? = null,
            val onSelectionChanged: (IEntity?) -> Unit
        ) : Dialog()
    }
}