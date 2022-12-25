package ui.utils.sampletypes_selector

import com.arkivanov.decompose.value.Value
import domain.SampleType
import kotlinx.coroutines.flow.StateFlow

interface ISampleTypesSelector {


    val state: StateFlow<State>

    fun selectType(type: SampleType?)

    data class State(
        val selectedType: SampleType? = null,
        val types: List<SampleType> = listOf()
    )
}