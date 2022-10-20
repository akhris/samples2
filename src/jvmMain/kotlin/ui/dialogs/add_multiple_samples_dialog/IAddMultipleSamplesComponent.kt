package ui.dialogs.add_multiple_samples_dialog

import com.arkivanov.decompose.value.Value

interface IAddMultipleSamplesComponent {

    val state: Value<State>

    fun parseIDs(rawString: String)

    data class State(
        val rawString: String = "",
        val parsedIDs: List<String> = listOf()
    )

}