package ui.dialogs.edit_sample_type_dialog

import com.arkivanov.decompose.value.Value
import domain.SampleType

interface IEditSampleTypeDialogComponent {

    val sampleType: Value<SampleType>

    val dialogType: Value<DialogType>

    val isChanged: Value<Boolean>
    fun updateSampleTypeInCache(type: SampleType)
    fun updateSampleTypeInStorage(type: SampleType)

    fun insertSampleType(type: SampleType)
    fun removeSampleType()
    sealed class DialogType {
        object Add : DialogType()
        object Edit : DialogType()
    }
}