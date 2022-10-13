package ui.dialogs.add_sample_type_dialog

import domain.SampleType

interface IAddSampleTypeDialogComponent {
    fun addSampleType(type: SampleType)
}