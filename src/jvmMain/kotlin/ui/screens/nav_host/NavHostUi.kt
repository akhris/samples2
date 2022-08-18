package ui.screens.nav_host

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.animation.child.childAnimation
import com.arkivanov.decompose.extensions.compose.jetbrains.animation.child.fade
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import strings.LocalizedStrings
import strings.defaultLocalizedStrings
import ui.screens.entities_screen.EntitiesScreenUi
import ui.screens.settings.SettingsUi

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun NavHostUi(component: INavHost, localizedStrings: LocalizedStrings = defaultLocalizedStrings) {

    val routerState by remember(component) { component.routerState }.subscribeAsState()

    Children(routerState = routerState, animation = childAnimation(fade())) {
        when (val child = remember(it) { it.instance }) {
            is INavHost.Child.Settings -> SettingsUi(child.component, localizedStrings)
            is INavHost.Child.EntitiesListWithSidePanel -> EntitiesScreenUi(child.component, localizedStrings)
        }
    }
}