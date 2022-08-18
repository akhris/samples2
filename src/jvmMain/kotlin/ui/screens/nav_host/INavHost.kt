package ui.screens.nav_host

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import ui.screens.rooms.IRooms

/**
 * Interface for Navigation Host
 */
interface INavHost {
//
//    /**
//     * Navigate to destination by route.
//     */
//    fun setDestination(route: String)

    /**
     * Exposes Router State
     */
    val childStack : Value<ChildStack<*, Child>>

    /**
     * Child classes containing child components.
     */
    sealed class Child {
        class Rooms(val component: IRooms) : Child()
//        class Samples(val component: ISamples) : Child()
//        class Workers(val component: IWorkers) : Child()
//        class Operations(val component: IOperations) : Child()
    }
}