package ui.screens.norms

import androidx.compose.foundation.layout.Column
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.*
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import ui.components.ChipGroup
import ui.components.FilterChip
import ui.screens.parameters.IParameters

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NormsUi(component: INorms) {
    val state by component.state.subscribeAsState()

    val conditions = remember(state) { state.norms.map { it.condition }.toSet() }

    var selectedConditionId by remember { mutableStateOf<String?>(null) }

    //render norms list:
    Column {
        //conditions row:


        ChipGroup {
            conditions.forEach { condition ->
                FilterChip(
                    text = condition.name,
                    isSelected = condition.id == selectedConditionId,
                    withCheckIcon = false,
                    onClick = {
                        selectedConditionId = condition.id
                    }
                )
            }
        }

        state
            .norms
            .filter { it.condition.id == selectedConditionId }
            .forEach { norm ->
                ListItem(overlineText = {
                    Text(norm.condition.name)
                }, text = {
                    Text("${norm.parameter.id}")
                }, secondaryText = {

                })
            }
    }
}