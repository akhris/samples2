package ui.root_ui

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import domain.*
import navigation.NavItem
import ui.dialogs.add_sample_type_dialog.IAddSampleTypeDialogComponent
import ui.screens.base_entity_screen.EntityComponentWithFab
import ui.screens.base_entity_screen.IEntityComponent
import ui.screens.preferences_screen.IPreferencesComponent
import ui.screens.sample_details_screen.ISampleDetailsComponent
import ui.toolbar_utils.sampletypes_selector.ISampleTypesSelector

interface IRootComponent {

    val navHostStack: Value<ChildStack<*, NavHost>>
    val dialogStack: Value<ChildStack<*, Dialog>>
    val toolbarUtilsStack: Value<ChildStack<*, ToolbarUtils>>

    val currentDestination: Value<NavItem>

    val currentDBPath: Value<String>

    fun navigateTo(navItem: NavItem)

    sealed class NavHost {
        class Places(val component: IEntityComponent<Place>) : NavHost()
        class Workers(val component: IEntityComponent<Worker>) : NavHost()
        class Operations(val component: EntityComponentWithFab<Operation>) : NavHost()
        class OperationTypes(val component: IEntityComponent<OperationType>) : NavHost()
        class Norms(val component: IEntityComponent<Norm>) : NavHost()
        class Parameters(val component: EntityComponentWithFab<Parameter>) : NavHost()
        class Samples(val component: EntityComponentWithFab<Sample>) : NavHost()
        class Measurements(val component: EntityComponentWithFab<Measurement>) : NavHost()
        class SampleDetails(val component: ISampleDetailsComponent) : NavHost()
        class AppPreferences(val component: IPreferencesComponent) : NavHost()
    }

    sealed class Dialog {
        class AddSampleTypeDialog(val component: IAddSampleTypeDialogComponent) : Dialog()
        object None : Dialog()
    }

    sealed class ToolbarUtils {
        class SampleTypesSelector(val component: ISampleTypesSelector) : ToolbarUtils()
    }

    fun showAddSampleTypeDialog()

    fun dismissDialog()
}