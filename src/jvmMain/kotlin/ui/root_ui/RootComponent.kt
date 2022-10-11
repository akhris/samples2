package ui.root_ui

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import org.kodein.di.DI
import ui.dialogs.add_sample_type_dialog.AddSampleTypeDialogComponent
import ui.nav_host.INavHost
import ui.nav_host.NavHostComponent

class RootComponent(
    private val di: DI,
    componentContext: ComponentContext
) : IRootComponent, ComponentContext by componentContext {

    private val dialogNav = StackNavigation<DialogConfig>()
    private val navHostNav = StackNavigation<NavHostConfig>()


    private val _navHostStack =
        childStack(
            source = navHostNav,
            initialConfiguration = NavHostConfig.NavHost,
//            handleBackButton = true,
            childFactory = ::createChild,
            key = "navhost stack"
        )

    private val _dialogStack =
        childStack(
            source = dialogNav,
            initialConfiguration = DialogConfig.None,
//            handleBackButton = true,
            childFactory = ::createChild,
            key = "dialog stack"
        )


    override val navHostStack: Value<ChildStack<*, IRootComponent.NavHost>>
        get() = _navHostStack

    override val dialogStack: Value<ChildStack<*, IRootComponent.Dialog>>
        get() = _dialogStack


    override fun showAddSampleTypeDialog() {
        dialogNav.replaceCurrent(DialogConfig.AddSampleType)
    }

    override fun dismissDialog() {
        dialogNav.replaceCurrent(DialogConfig.None)
    }


    private fun createChild(dialogConfig: DialogConfig, componentContext: ComponentContext): IRootComponent.Dialog {
        return when (dialogConfig) {
            DialogConfig.AddSampleType -> IRootComponent.Dialog.AddSampleTypeDialog(
                AddSampleTypeDialogComponent(
                    di = di,
                    componentContext = componentContext
                )
            )

            DialogConfig.None -> IRootComponent.Dialog.None
        }
    }

    private fun createChild(navhostConfig: NavHostConfig, componentContext: ComponentContext): IRootComponent.NavHost {
        return when (navhostConfig) {
            NavHostConfig.NavHost -> IRootComponent.NavHost.MainNavHost(
                NavHostComponent(
                    di = di,
                    componentContext = componentContext
                )
            )
        }
    }

    @Parcelize
    private sealed class DialogConfig : Parcelable {

        @Parcelize
        object AddSampleType : DialogConfig()

        @Parcelize
        object None : DialogConfig()

    }

    @Parcelize
    private sealed class NavHostConfig : Parcelable {
        @Parcelize
        object NavHost : NavHostConfig()

    }
}