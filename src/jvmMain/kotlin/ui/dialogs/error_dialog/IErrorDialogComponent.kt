package ui.dialogs.error_dialog

import com.arkivanov.decompose.value.Value

interface IErrorDialogComponent {
    val state: Value<State>

    data class State(
        val title: String = "",
        val caption: String = "",
        val error: Throwable? = null
    )
}