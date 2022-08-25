package ui.screens.parameters

import com.arkivanov.decompose.value.Value
import domain.Parameter

interface IParameters {

    val state: Value<State>

    fun addNewParameter(parameter: Parameter)

    fun removeParameter(parameter: Parameter)

    fun updateParameter(parameter: Parameter)

    data class State(
        val parameters: List<Parameter> = listOf()
    )
}