package ui.screens.norms

import com.arkivanov.decompose.value.Value
import domain.Norm

interface INorms {

    val state: Value<State>

    data class State(
        val norms: List<Norm> = listOf()
    )
}