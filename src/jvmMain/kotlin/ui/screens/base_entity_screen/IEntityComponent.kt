package ui.screens.base_entity_screen

import com.arkivanov.decompose.value.Value
import domain.IEntity

interface IEntityComponent<T : IEntity> {
    val state: Value<State<T>>

    fun insertNewEntity(entity: T)
    fun updateEntity(entity: T)


    data class State<E : IEntity>(
        val entities: List<E> = listOf()
    )
}