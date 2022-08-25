package ui

import LocalSamplesType
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import test.SampleTypes
import ui.components.SampleTypeSelector
import ui.screens.nav_host.INavHost
import ui.screens.nav_host.NavHostUi

@Composable
fun RootUi(component: INavHost) {

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed, confirmStateChange = { false })
    val scaffoldState = rememberScaffoldState(drawerState = drawerState)

    val navigationItem by remember(component) { component.state }.subscribeAsState()
    val sampleTypes by remember(component) { component.sampleTypes }.subscribeAsState()
    var selectedSampleType by remember { mutableStateOf(SampleTypes.type1) }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar {
//                Text(modifier = Modifier.padding(start = UiSettings.AppBar.titleStartPadding).weight(1f), text = navigationItem.currentDestination?.title?:"")
                SampleTypeSelector(
                    modifier = Modifier.padding(start = UiSettings.AppBar.titleStartPadding).weight(1f),
                    typesList = sampleTypes,
                    selectedType = selectedSampleType,
                    onSampleTypeSelected = { selectedSampleType = it },
                    onNewSampleTypeAdd = {
                        component.addSampleType(it)
                    },
                    onSampleTypeDelete = {
                        component.removeSampleType(it)
                    })
            }
        },
        content = {
            CompositionLocalProvider(LocalSamplesType provides selectedSampleType) {
                Row {
                    SideNavigationPanel(
                        isExpandable = false,
                        withLabels = true,
                        currentSelection = navigationItem.currentDestination,
                        onNavigationItemSelected = { component.setDestination(it) })

                    Box(modifier = Modifier.weight(1f)) { NavHostUi(component = component) }
                }
            }
        }
    )

//    Row(modifier = Modifier.background(MaterialTheme.colors.background)) {
//
////        NavigationRailUi(NavigationRailComponent(onNavigateTo = {
////            component.setDestination(it.route)
////        }, onAddButtonClicked = {
////            addClickedNavItem = it
////        }), localizedStrings = localizedStrings)
//
//        Box(modifier = Modifier.fillMaxHeight().weight(1f)) {
//
//        }
//    }
}