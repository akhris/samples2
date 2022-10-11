package ui.nav_host

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import domain.*
import navigation.NavItem
import ui.dialogs.add_sample_type_dialog.IAddSampleTypeDialogComponent
import ui.screens.base_entity_screen.EntityComponentWithFab
import ui.screens.base_entity_screen.IEntityComponent
import ui.screens.sample_details_screen.ISampleDetailsComponent

/**
 * Interface for Navigation Host
 */
interface INavHost {
    fun setDestination(navItem: NavItem)

    val state: Value<State>

    //fixme move sampletypes to special selector component

    val sampleTypes: Value<List<SampleType>>
    fun addSampleType(type: SampleType)
    fun removeSampleType(type: SampleType)

    data class State(
        val currentDestination: NavItem? = NavItem.homeItem
    )

    /**
     * Exposes Router State
     */
    val childStack: Value<ChildStack<*, Child>>

    /**
     * Child classes containing child components.
     */
    sealed class Child {
        class Places(val component: IEntityComponent<Place>) : Child()

        //        class Samples(val component: ISamples) : Child()
        class Workers(val component: IEntityComponent<Worker>) : Child()
        class Operations(val component: EntityComponentWithFab<Operation>) : Child()
        class OperationTypes(val component: IEntityComponent<OperationType>) : Child()
        class Norms(val component: IEntityComponent<Norm>) : Child()
        class Parameters(val component: EntityComponentWithFab<Parameter>) : Child()
        class Samples(val component: EntityComponentWithFab<Sample>) : Child()
        class Measurements(val component: EntityComponentWithFab<Measurement>) : Child()
        class SampleDetails(val component: ISampleDetailsComponent) : Child()

    }


}