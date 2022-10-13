package ui.root_ui

import LocalSamplesType
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import domain.SampleType
import ui.SideNavigationPanel
import ui.dialogs.add_sample_type_dialog.AddSampleTypeDialogUi
import ui.screens.base_entity_screen.BaseEntityUi
import ui.screens.base_entity_screen.EntityUiwithFab
import ui.screens.sample_details_screen.SampleDetailsUi
import ui.toolbar_utils.sampletypes_selector.SampleTypesSelectorUi

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun RootUi(component: IRootComponent, isDarkTheme: Boolean, onThemeChanged: (isDark: Boolean) -> Unit) {

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed, confirmStateChange = { false })
    val scaffoldState = rememberScaffoldState(drawerState = drawerState)

    val navigationItem by remember(component) { component.currentDestination }.subscribeAsState()
//    val sampleTypes by remember(component) { component.sampleTypes }.subscribeAsState()
    var selectedSampleType by remember { mutableStateOf<SampleType?>(null) }


    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar {
                Spacer(modifier = Modifier.weight(1f))
                Children(stack = component.toolbarUtilsStack) {
                    when (val child = it.instance) {
                        is IRootComponent.ToolbarUtils.SampleTypesSelector -> SampleTypesSelectorUi(
                            component = child.component,
                            onSampleTypeSelected = {
                                selectedSampleType = it
                            }, onAddNewSampleTypeClick = {
                                component.showAddSampleTypeDialog()
                            })
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    modifier = Modifier
                        .padding(horizontal = 8.dp).clickable {
                            onThemeChanged(!isDarkTheme)
                        },
                    painter = when (isDarkTheme) {
                        true -> painterResource("vector/light_mode_black_24dp.svg")
                        false -> painterResource("vector/dark_mode_black_24dp.svg")
                    }, contentDescription = "light/dark theme switcher"
                )
            }
        },
        content = {

            CompositionLocalProvider(LocalSamplesType provides selectedSampleType) {
                Row {
                    SideNavigationPanel(
                        isExpandable = false,
                        withLabels = true,
                        currentSelection = navigationItem,
                        onNavigationItemSelected = { component.navigateTo(it) })
                    Box(
                        modifier = Modifier.weight(1f)
                    ) {
                        Children(stack = component.navHostStack, animation = stackAnimation(fade())) {
                            when (val child = it.instance) {
                                is IRootComponent.NavHost.Measurements -> EntityUiwithFab(component = child.component)
                                is IRootComponent.NavHost.Norms -> BaseEntityUi(component = child.component)
                                is IRootComponent.NavHost.OperationTypes -> BaseEntityUi(component = child.component)
                                is IRootComponent.NavHost.Operations -> EntityUiwithFab(component = child.component)
                                is IRootComponent.NavHost.Parameters -> EntityUiwithFab(component = child.component)
                                is IRootComponent.NavHost.Places -> BaseEntityUi(component = child.component)
                                is IRootComponent.NavHost.SampleDetails -> SampleDetailsUi(component = child.component)
                                is IRootComponent.NavHost.Samples -> EntityUiwithFab(component = child.component)
                                is IRootComponent.NavHost.Workers -> BaseEntityUi(component = child.component)
                            }
                        }
                    }
                }
            }
        }
    )

    Children(stack = component.dialogStack, animation = stackAnimation(slide())) {
        when (val child = it.instance) {
            IRootComponent.Dialog.None -> {}
            is IRootComponent.Dialog.AddSampleTypeDialog -> AddSampleTypeDialogUi(child.component, onDismiss = {
                component.dismissDialog()
            })
        }
    }


}