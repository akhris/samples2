package ui.root_ui

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.reduce
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import domain.Sample
import domain.SampleType
import navigation.NavItem
import org.kodein.di.DI
import org.kodein.di.instance
import settings.PreferencesManager
import ui.dialogs.edit_sample_type_dialog.EditSampleTypeDialogComponent
import ui.screens.base_entity_screen.EntityComponent
import ui.screens.base_entity_screen.entityComponents.MeasurementsEntityComponent
import ui.screens.base_entity_screen.entityComponents.OperationsComponent
import ui.screens.base_entity_screen.entityComponents.ParametersComponent
import ui.screens.base_entity_screen.entityComponents.SamplesComponent
import ui.screens.preferences_screen.PreferencesComponent
import ui.screens.sample_details_screen.SampleDetailsComponent
import ui.utils.sampletypes_selector.SampleTypesSelectorComponent

class RootComponent(
    private val di: DI,
    componentContext: ComponentContext
) : IRootComponent, ComponentContext by componentContext {

    private val dialogNav = StackNavigation<DialogConfig>()
    private val navHostNav = StackNavigation<NavHostConfig>()
    private val toolbarUtilsNav = StackNavigation<ToolbarUtilsConfig>()
//    private val preferencesManager: PreferencesManager by di.instance()
    private val _currentDestination = MutableValue<NavItem>(NavItem.homeItem)
    override val currentDestination: Value<NavItem> = _currentDestination

    private val _navHostStack =
        childStack(
            source = navHostNav,
            initialConfiguration = NavHostConfig.Samples,
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

    private val _toolbarUtilsStack =
        childStack(
            source = toolbarUtilsNav,
            initialConfiguration = ToolbarUtilsConfig.SampleTypesSelector,
            childFactory = ::createChild,
            key = "toolbar utils stack"
        )

    override val navHostStack: Value<ChildStack<*, IRootComponent.NavHost>> = _navHostStack
    override val dialogStack: Value<ChildStack<*, IRootComponent.Dialog>> = _dialogStack
    override val toolbarUtilsStack: Value<ChildStack<*, IRootComponent.ToolbarUtils>> = _toolbarUtilsStack

    override fun showEditSampleTypeDialog(sampleType: SampleType?) {
        dialogNav.replaceCurrent(DialogConfig.AddSampleType(sampleType))
    }

    override fun dismissDialog() {
        dialogNav.replaceCurrent(DialogConfig.None)
    }


    private fun createChild(config: DialogConfig, componentContext: ComponentContext): IRootComponent.Dialog {
        return when (config) {
            is DialogConfig.AddSampleType -> IRootComponent.Dialog.AddSampleTypeDialog(
                EditSampleTypeDialogComponent(
                    di = di,
                    componentContext = componentContext,
                    initialSampleType = config.sampleType
                )
            )

            DialogConfig.None -> IRootComponent.Dialog.None
        }
    }

    private fun createChild(config: NavHostConfig, componentContext: ComponentContext): IRootComponent.NavHost {
        return when (config) {
            NavHostConfig.Measurements -> IRootComponent.NavHost.Measurements(
                MeasurementsEntityComponent(
                    di = di,
                    componentContext = componentContext
                )
            )

            NavHostConfig.Norms -> IRootComponent.NavHost.Norms(EntityComponent(di = di, componentContext))
            NavHostConfig.OperationTypes -> IRootComponent.NavHost.OperationTypes(
                EntityComponent(
                    di = di,
                    componentContext
                )
            )

            NavHostConfig.Operations -> IRootComponent.NavHost.Operations(
                OperationsComponent(
                    di = di,
                    componentContext
                )
            )

            NavHostConfig.Parameters -> IRootComponent.NavHost.Parameters(
                ParametersComponent(
                    di = di,
                    componentContext
                )
            )

            NavHostConfig.Places -> IRootComponent.NavHost.Places(EntityComponent(di = di, componentContext))
            is NavHostConfig.SampleDetails -> IRootComponent.NavHost.SampleDetails(
                SampleDetailsComponent(
                    di = di,
                    componentContext = componentContext,
                    sample = config.sample
                )
            )

            NavHostConfig.Workers -> IRootComponent.NavHost.Workers(EntityComponent(di = di, componentContext))
            NavHostConfig.Samples -> IRootComponent.NavHost.Samples(
                SamplesComponent(
                    di = di,
                    componentContext,
                    onSampleSelected = {
                        //navigate to sample details here
                        navigateTo(NavItem.SampleDetails(it))
                    })
            )

            NavHostConfig.AppPreferences -> IRootComponent.NavHost.AppPreferences(
                PreferencesComponent(
                    di = di,
                    componentContext = componentContext
                )
            )
        }
    }


    private fun createChild(
        config: ToolbarUtilsConfig,
        componentContext: ComponentContext
    ): IRootComponent.ToolbarUtils {
        return when (config) {
            ToolbarUtilsConfig.SampleTypesSelector -> IRootComponent.ToolbarUtils.SampleTypesSelector(
                SampleTypesSelectorComponent(di = di, componentContext = componentContext)
            )
        }
    }


    override fun navigateTo(navItem: NavItem) {
        val newConf = when (navItem) {
            NavItem.Conditions -> null
            NavItem.Measurements -> NavHostConfig.Measurements
            NavItem.Norms -> NavHostConfig.Norms
            NavItem.Operations -> NavHostConfig.Operations
            NavItem.Parameters -> NavHostConfig.Parameters
            NavItem.Places -> NavHostConfig.Places
            NavItem.SampleTypes -> null
            NavItem.Samples -> NavHostConfig.Samples
            NavItem.Workers -> NavHostConfig.Workers
            NavItem.OperationTypes -> NavHostConfig.OperationTypes
            NavItem.AppSettings -> NavHostConfig.AppPreferences
            is NavItem.SampleDetails -> NavHostConfig.SampleDetails(navItem.sample)
        }
        if (newConf != null && newConf != _navHostStack.value.active.configuration) {
            navHostNav.replaceCurrent(newConf)
            _currentDestination.reduce { navItem }
        }
    }

    @Parcelize
    private sealed class DialogConfig : Parcelable {

        @Parcelize
        class AddSampleType(val sampleType: SampleType? = null) : DialogConfig()

        @Parcelize
        object None : DialogConfig()

    }

    @Parcelize
    private sealed class NavHostConfig : Parcelable {
        @Parcelize
        object Places : NavHostConfig()

        @Parcelize
        object Workers : NavHostConfig()

        @Parcelize
        object Norms : NavHostConfig()

        @Parcelize
        object Parameters : NavHostConfig()

        @Parcelize
        object Operations : NavHostConfig()

        @Parcelize
        object OperationTypes : NavHostConfig()

        @Parcelize
        data class SampleDetails(val sample: Sample) : NavHostConfig()

        @Parcelize
        object Samples : NavHostConfig()

        @Parcelize
        object Measurements : NavHostConfig()

        @Parcelize
        object AppPreferences : NavHostConfig()

    }

    @Parcelize
    private sealed class ToolbarUtilsConfig : Parcelable {
        object SampleTypesSelector : ToolbarUtilsConfig()
    }

}