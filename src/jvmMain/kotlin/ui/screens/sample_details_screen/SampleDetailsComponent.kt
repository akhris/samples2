package ui.screens.sample_details_screen

import com.arkivanov.decompose.ComponentContext
import org.kodein.di.DI

class SampleDetailsComponent(
    private val di: DI,
    componentContext: ComponentContext
) : ISampleDetailsComponent, ComponentContext by componentContext