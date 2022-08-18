package ui.screens.nav_host

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import ui.screens.rooms.RoomsComponent

/**
 * Main navigation component that holds all destinations
 */
class NavHostComponent constructor(
    componentContext: ComponentContext,
) :
    INavHost, ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    private val stack =
        childStack(
            source = navigation,
            initialConfiguration = Config.Rooms,
            handleBackButton = true,
            childFactory = ::createChild
        )

    override val childStack: Value<ChildStack<*, INavHost.Child>>
        get() = stack

    private fun createChild(config: Config, componentContext: ComponentContext): INavHost.Child {
        return when (config) {
            Config.Rooms -> INavHost.Child.Rooms(RoomsComponent(componentContext))
        }
    }

    @Parcelize
    private sealed class Config : Parcelable {
        @Parcelize
        object Rooms : Config()
    }

}