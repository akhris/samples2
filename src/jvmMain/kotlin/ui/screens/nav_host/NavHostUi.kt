package ui.screens.nav_host

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.stackAnimation
import ui.screens.norms.NormsUi
import ui.screens.operationtypes.OperationsTypesUi
import ui.screens.parameters.ParametersUi
import ui.screens.places.PlacesUi
import ui.screens.samples.SamplesUi
import ui.screens.workers.WorkersUi


@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun NavHostUi(component: INavHost) {
    Children(stack = component.childStack, animation = stackAnimation(fade())) {
        when (val child = it.instance) {
            is INavHost.Child.Places -> PlacesUi(child.component)
            is INavHost.Child.Workers -> WorkersUi(child.component)
            is INavHost.Child.Norms -> NormsUi(child.component)
            is INavHost.Child.Parameters -> ParametersUi(child.component)
            is INavHost.Child.Operations -> OperationsTypesUi(child.component)
            is INavHost.Child.Samples -> SamplesUi(child.component)
        }
    }
}