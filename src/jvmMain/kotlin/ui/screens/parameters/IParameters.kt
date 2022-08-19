package ui.screens.parameters

import com.arkivanov.decompose.value.Value
import domain.Parameter

interface IParameters {

    val state: Value<State>

    data class State(
        val parameters: List<Parameter> = listOf()
    )
}