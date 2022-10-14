package ui.dialogs.list_picker_dialog

import com.arkivanov.decompose.value.Value

interface IListPickerDialogComponent {

    val state: Value<State>

    data class State(
        val title: String = "",
        val items: List<ListPickerItem> = listOf(),
        val mode: ListPickerMode = ListPickerMode.SingleSelect()
    )
}