package ui.screens.base_entity_screen

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import domain.IEntity
import ui.components.tables.IDataTableMapper
import ui.screens.nav_host.INavHost

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


    sealed class Dialog {
        object None : Dialog()
        class EntityPicker<T : IEntity>(component: IEntityComponent<T>) : Dialog()
    }
}