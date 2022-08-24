package ui.screens.nav_host

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import domain.SampleType
import navigation.NavItem
import ui.screens.norms.INorms
import ui.screens.parameters.IParameters
import ui.screens.operationtypes.IOperationTypes
import ui.screens.places.IPlaces
import ui.screens.samples.ISamples
import ui.screens.workers.IWorkers

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
        class Places(val component: IPlaces) : Child()

        //        class Samples(val component: ISamples) : Child()
        class Workers(val component: IWorkers) : Child()
        class Operations(val component: IOperationTypes) : Child()
        class Norms(val component: INorms) : Child()
        class Parameters(val component: IParameters) : Child()
        class Samples(val component: ISamples) : Child()

    }
}