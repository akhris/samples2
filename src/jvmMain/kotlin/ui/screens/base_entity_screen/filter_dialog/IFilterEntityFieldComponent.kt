package ui.screens.base_entity_screen.filter_dialog

import com.arkivanov.decompose.value.Value
import domain.FilterSpec
import domain.IEntity

interface IFilterEntityFieldComponent<T : IEntity> {
    val filterSpec: Value<FilterSpec>

    val slice: Value<List<String>>
}