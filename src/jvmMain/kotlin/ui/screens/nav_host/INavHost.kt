package ui.screens.nav_host

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import domain.*
import navigation.NavItem
import ui.screens.base_entity_screen.IEntityComponent
import ui.screens.measurements.IMeasurements

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
        class Operations(val component: IEntityComponent<Operation>) : Child()
        class OperationTypes(val component: IEntityComponent<OperationType>) : Child()
        class Norms(val component: IEntityComponent<Norm>) : Child()
        class Parameters(val component: IEntityComponent<Parameter>) : Child()
        class Samples(val component: IEntityComponent<Sample>) : Child()
        class Measurements(val component: IMeasurements) : Child()


    }

}