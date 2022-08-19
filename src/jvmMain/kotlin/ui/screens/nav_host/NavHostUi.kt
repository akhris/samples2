package ui.screens.nav_host

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.stackAnimation
import ui.screens.places.PlacesUi
import ui.screens.workers.WorkersUi


@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun NavHostUi(component: INavHost) {
    Children(stack = component.childStack, animation = stackAnimation(fade())) {
        when (val child = it.instance) {
            is INavHost.Child.Places -> PlacesUi(child.component)
            is INavHost.Child.Workers -> WorkersUi(child.component)
        }
    }
}