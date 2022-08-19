package ui.screens.operationtypes

import com.arkivanov.decompose.value.Value
import domain.OperationType

interface IOperationTypes {

    val state: Value<State>

    data class State(
        val operationTypes: List<OperationType> = listOf()
    )
}