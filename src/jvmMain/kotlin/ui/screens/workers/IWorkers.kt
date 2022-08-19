package ui.screens.workers

import com.arkivanov.decompose.value.Value
import domain.Worker

interface IWorkers {

    val state: Value<State>

    data class State(
        val rooms: List<Worker> = listOf()
    )
}