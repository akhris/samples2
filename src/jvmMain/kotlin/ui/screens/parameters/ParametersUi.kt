package ui.screens.parameters

import androidx.compose.foundation.layout.Column
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.*
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import ui.components.ChipGroup
import ui.components.FilterChip

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ParametersUi(component: IParameters) {
    val state by component.state.subscribeAsState()

        //render norms list:
    Column {
        //conditions row:


        state
            .parameters
            .forEach { parameter ->
                ListItem(overlineText = {
                    Text(parameter.parameterID)
                }, text = {
                    Text("${parameter.description}")
                }, secondaryText = {

                })
            }
    }
}