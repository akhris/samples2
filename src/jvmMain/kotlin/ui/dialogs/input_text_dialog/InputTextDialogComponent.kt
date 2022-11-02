package ui.dialogs.input_text_dialog

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.reduce
import org.kodein.di.DI

class InputTextDialogComponent(
    title: String,
    caption: String,
    private val doOnConfirm: (String) -> Unit,
    di: DI,
    componentContext: ComponentContext
) : IInputTextDialog, ComponentContext by componentContext {

    private val _state = MutableValue(IInputTextDialog.State(title = title, caption = caption))
    override val state: Value<IInputTextDialog.State> = _state

    override fun confirm() {
        doOnConfirm(state.value.text)
    }

    override fun changeText(text: String) {
        _state.reduce { it.copy(text = text) }
    }

}