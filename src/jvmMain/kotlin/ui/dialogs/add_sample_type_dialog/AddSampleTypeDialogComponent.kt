package ui.dialogs.add_sample_type_dialog

import com.arkivanov.decompose.ComponentContext
import org.kodein.di.DI

class AddSampleTypeDialogComponent(
    di: DI,
    componentContext: ComponentContext
) : IAddSampleTypeDialogComponent, ComponentContext by componentContext