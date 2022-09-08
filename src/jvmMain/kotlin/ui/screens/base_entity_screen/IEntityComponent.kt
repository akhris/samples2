package ui.screens.base_entity_screen

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import domain.EntitiesList
import domain.IEntity
import domain.SampleType
import domain.Specification
import ui.components.tables.IDataTableMapper
import kotlin.reflect.KClass

interface IEntityComponent<T : IEntity> {
    val state: Value<State<T>>

    fun insertNewEntity(entity: T)
    fun insertNewEntity(sampleType: SampleType)
    fun updateEntity(entity: T)

    fun duplicateEntities(entities: List<T>)

    val dataMapper: IDataTableMapper<T>

    fun setQuerySpec(spec: Specification)
    fun resetQuerySpec(spec: Specification)

    fun saveRowsToExcel(entities: List<T>)

    data class State<E : IEntity>(
        val entities: EntitiesList<E> = EntitiesList.empty()
    )

    /**
     * Exposes Router State
     */
    val dialogStack: Value<ChildStack<*, Dialog>>

    fun dismissDialog()

    fun showEntityPickerDialog(
        entity: IEntity?,
        entityClass: KClass<out IEntity>,
        onSelectionChanged: (IEntity?) -> Unit,
        columnName: String
    )

    sealed class Dialog {
        object None : Dialog()
        class EntityPicker<T : IEntity>(
            val component: IEntityComponent<T>,
            val initialSelection: String? = null,
            val onSelectionChanged: (IEntity?) -> Unit,
            val columnName: String = ""
        ) : Dialog()
    }
}