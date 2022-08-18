package ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ui.screens.nav_host.INavHost
import ui.screens.nav_host.NavHostUi

@Composable
fun RootUi(component: INavHost){
    Row(modifier = Modifier.background(MaterialTheme.colors.background)) {
//        NavigationRailUi(NavigationRailComponent(onNavigateTo = {
//            component.setDestination(it.route)
//        }, onAddButtonClicked = {
//            addClickedNavItem = it
//        }), localizedStrings = localizedStrings)

        Box(modifier = Modifier.fillMaxHeight().weight(1f)) {
            NavHostUi(component = component)
        }
    }
}