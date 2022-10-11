package ui.root_ui

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import ui.dialogs.add_sample_type_dialog.IAddSampleTypeDialogComponent
import ui.nav_host.INavHost

interface IRootComponent {

    val navHostStack: Value<ChildStack<*, NavHost>>
    val dialogStack: Value<ChildStack<*, Dialog>>

    sealed class NavHost {
        class MainNavHost(val component: INavHost) : NavHost()
    }

    sealed class Dialog {
        class AddSampleTypeDialog(val component: IAddSampleTypeDialogComponent) : Dialog()
        object None : Dialog()
    }

    fun showAddSampleTypeDialog()

    fun dismissDialog()
}