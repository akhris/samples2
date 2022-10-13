package ui.dialogs.prompt_dialog

import com.arkivanov.decompose.value.Value

interface IPromptDialog {
    val state: Value<State>

    data class State(
        val title: String,
        val message: String
    )
}