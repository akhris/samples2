package ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import navigation.NavItem

@Composable
fun SideNavigationPanel(
    isExpandable: Boolean = false,
    withLabels: Boolean = false,
    currentSelection: NavItem? = null,
    onNavigationItemSelected: (NavItem) -> Unit
) {

    var isExpanded by remember { mutableStateOf(false) }

    val panelWidth by animateDpAsState(
        when (isExpanded) {
            true -> UiSettings.NavigationPanel.widthExpanded
            false -> UiSettings.NavigationPanel.widthCollapsed
        }
    )

    NavigationRail(
        elevation = UiSettings.NavigationPanel.elevation,
        modifier = Modifier.width(panelWidth),
        header = if (isExpandable) {
            {
                IconButton(
                    modifier = Modifier.align(Alignment.End),
                    onClick = {
                        isExpanded = !isExpanded
                    },
                    content = {
                        Icon(
                            imageVector = when (isExpanded) {
                                true -> Icons.Rounded.KeyboardArrowLeft
                                false -> Icons.Rounded.KeyboardArrowRight
                            },
                            contentDescription = "expand or collapse icon"
                        )
                    }
                )
            }
        } else null
    ) {
        NavItem
            .getMainNavigationItems()
            .forEach { navItem ->
                NavigationRailItem(
                    selected = navItem == currentSelection,
                    alwaysShowLabel = panelWidth == UiSettings.NavigationPanel.widthExpanded,
                    icon =
                    {
                        Icon(
                            painter = painterResource(navItem.pathToIcon),
                            contentDescription = navItem.title,
                            modifier = Modifier.size(UiSettings.NavigationPanel.iconSize)
                        )

                    },
                    label = if (withLabels) {
                        { Text(maxLines = 1, overflow = TextOverflow.Ellipsis, text = navItem.title) }
                    } else null,
                    onClick = {
                        onNavigationItemSelected(navItem)
                    })
            }
    }
}