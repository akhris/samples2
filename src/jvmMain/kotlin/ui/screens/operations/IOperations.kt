package ui.screens.operations

import com.arkivanov.decompose.value.Value
import domain.Operation

interface IOperations {
    val state: Value<State>

    fun updateOperation(operation: Operation)
    fun insertOperation(operation: Operation)


    data class State(
        val operations: List<Operation> = listOf()
    )
}