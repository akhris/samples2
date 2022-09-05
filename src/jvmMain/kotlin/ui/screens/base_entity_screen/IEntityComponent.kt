package ui.screens.base_entity_screen

import com.arkivanov.decompose.value.Value
import domain.IEntity
import ui.components.tables.IDataTableMapper

interface IEntityComponent<T : IEntity> {
    val state: Value<State<T>>

    fun insertNewEntity(entity: T)
    fun updateEntity(entity: T)

    val dataMapper: IDataTableMapper<T>

    data class State<E : IEntity>(
        val entities: List<E> = listOf()
    )
}