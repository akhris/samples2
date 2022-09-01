package ui

import LocalSamplesType
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.rememberDialogState
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import domain.SampleType
import ui.components.ListSelector
import ui.screens.nav_host.INavHost
import ui.screens.nav_host.NavHostUi

@Composable
fun RootUi(component: INavHost) {

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed, confirmStateChange = { false })
    val scaffoldState = rememberScaffoldState(drawerState = drawerState)

    val navigationItem by remember(component) { component.state }.subscribeAsState()
    val sampleTypes by remember(component) { component.sampleTypes }.subscribeAsState()
    var selectedSampleType by remember { mutableStateOf<SampleType?>(sampleTypes.firstOrNull()) }

    var showNewSampleTypeDialog by remember { mutableStateOf(false) }
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar {
//                Text(modifier = Modifier.padding(start = UiSettings.AppBar.titleStartPadding).weight(1f), text = navigationItem.currentDestination?.title?:"")
                ListSelector(
                    modifier = Modifier.padding(start = UiSettings.AppBar.titleStartPadding).weight(1f),
                    currentSelection = selectedSampleType,
                    items = sampleTypes,
                    onAddNewClicked = { showNewSampleTypeDialog = true },
                    onItemDelete = { component.removeSampleType(it) },
                    onItemSelected = { selectedSampleType = it },
                    itemName = { it.name },
                    title = "Тип образцов"
                )


//                SampleTypeSelector(
//                    modifier = Modifier.padding(start = UiSettings.AppBar.titleStartPadding).weight(1f),
//                    typesList = sampleTypes,
//                    selectedType = selectedSampleType,
//                    onSampleTypeSelected = { selectedSampleType = it },
//                    onNewSampleTypeAdd = {
//                        component.addSampleType(it)
//                        selectedSampleType = it
//                    },
//                    onSampleTypeDelete = {
//                        if (selectedSampleType == it) {
//                            //change selection:
//                            val removedIndex = sampleTypes.indexOf(it)
//                            selectedSampleType = if (sampleTypes.size > 1) {
//                                if (removedIndex == 0) {
//                                    sampleTypes[1]
//                                } else {
//                                    sampleTypes.getOrNull(removedIndex - 1)
//                                }
//                            } else {
//                                null
//                            }
//                        }
//                        component.removeSampleType(it)
//                    })
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

    if (showNewSampleTypeDialog) {

        var newSampleTypeName by remember { mutableStateOf("") }

        Dialog(
            state = rememberDialogState(),
            onCloseRequest = { showNewSampleTypeDialog = false },
            content = {
                Column {
                    TextField(
                        value = newSampleTypeName,
                        onValueChange = { newSampleTypeName = it },
                        label = { Text("Имя типа образцов") })
                    Button(onClick = {
                        if (newSampleTypeName.isNotEmpty()) {
                            val newSampleType = SampleType(name = newSampleTypeName)
                            component.addSampleType(
                                newSampleType
                            )
                            selectedSampleType = newSampleType
                            showNewSampleTypeDialog = false
                        }
                    }, content = { Text("Добавить") })
                }
            })
    }

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