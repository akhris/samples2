package ui.screens.samples

import com.arkivanov.decompose.value.Value
import domain.Sample

interface ISamples {

    val state: Value<State>

    data class State(
        val samples: List<Sample> = listOf()
    )
}