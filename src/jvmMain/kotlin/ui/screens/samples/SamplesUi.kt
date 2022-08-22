package ui.screens.samples

import LocalSamplesType
import androidx.compose.foundation.layout.Column
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.*
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SamplesUi(component: ISamples) {
    val state by component.state.subscribeAsState()

    //render samples list:
    Column {
        state
            .samples
            .filter { it.type == LocalSamplesType.current }
            .forEach { sample ->
                ListItem(overlineText = {
                    Text(sample.id)
                }, text = {
                    Text("${sample.description}")
                }, secondaryText = {

                })
            }
    }
}