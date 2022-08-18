package ui.screens.nav_host

import com.arkivanov.decompose.router.RouterState
import com.arkivanov.decompose.value.Value
import ui.screens.entities_screen.IEntitiesScreen
import ui.screens.settings.ISettings

/**
 * Interface for Navigation Host
 */
interface INavHost {

    /**
     * Navigate to destination by route.
     */
    fun setDestination(route: String)

    /**
     * Exposes Router State
     */
    val routerState: Value<RouterState<*, Child>>

    /**
     * Child classes containing child components.
     */
    sealed class Child {
        data class Settings(val component: ISettings) : Child()
        data class EntitiesListWithSidePanel(val component: IEntitiesScreen) : Child()
    }
}