package ui.utils.sampletypes_selector

import com.arkivanov.decompose.value.Value
import domain.SampleType

interface ISampleTypesSelector {


    val state: Value<State>

    fun selectType(type: SampleType?)

    data class State(
        val selectedType: SampleType? = null,
        val types: List<SampleType> = listOf()
    )
}