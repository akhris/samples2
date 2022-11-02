package ui.dialogs.input_text_dialog

import com.arkivanov.decompose.value.Value

interface IInputTextDialog {

    val state: Value<State>

    fun changeText(text: String)

    fun confirm()

    data class State(
        val title: String = "",
        val caption: String = "",
        val text: String = ""
    )
}