package ui.dialogs.list_picker_dialog

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import org.kodein.di.DI

class ListPickerDialogComponent(
    private val di: DI,
    componentContext: ComponentContext,
    dialogTitle: String,
    val items: List<ListPickerItem> = listOf(),
    val selectMode: ListPickerMode = ListPickerMode.SingleSelect(),
) : IListPickerDialogComponent,
    ComponentContext by componentContext {

    private val _state =
        MutableValue(
            IListPickerDialogComponent.State(
                title = dialogTitle,
                items = items,
                mode = selectMode
            )
        )

    override val state: Value<IListPickerDialogComponent.State> = _state

}