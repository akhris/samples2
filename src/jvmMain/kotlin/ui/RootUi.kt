package ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ui.screens.nav_host.INavHost
import ui.screens.nav_host.NavHostUi

@Composable
fun RootUi(component: INavHost) {

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed, confirmStateChange = { false })
    val scaffoldState = rememberScaffoldState(drawerState = drawerState)
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar { Text("title") }
        },
        content = {
            Row {
                NavigationRail {
                    NavigationRailItem(
                        selected = true,
                        icon = { Icons.Rounded.Person },
                        label = { Text("item1") },
                        onClick = {})
                    NavigationRailItem(
                        selected = false,
                        icon = { Icons.Rounded.Email },
                        label = { Text("item2") },
                        onClick = {})
                    NavigationRailItem(
                        selected = false,
                        icon = { Icons.Rounded.Check },
                        label = { Text("item3") },
                        onClick = {})
                }
                Box(modifier = Modifier.weight(1f)) { NavHostUi(component = component) }
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