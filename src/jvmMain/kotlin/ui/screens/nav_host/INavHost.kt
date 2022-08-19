package ui.screens.nav_host

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import navigation.NavItem
import ui.screens.places.IPlaces
import ui.screens.workers.IWorkers

/**
 * Interface for Navigation Host
 */
interface INavHost {
    fun setDestination(navItem: NavItem)

    val state: Value<State>

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
//        class Operations(val component: IOperations) : Child()
    }
}