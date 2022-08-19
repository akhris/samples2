package ui.screens.operationtypes

import androidx.compose.foundation.layout.Column
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun OperationsTypesUi(component: IOperationTypes) {
    val state by component.state.subscribeAsState()

    //render rooms list:
    Column {
        state.operationTypes.forEach { operation->
            ListItem(overlineText = {
                Text(operation.name)
            }, text = {
                Text(operation.description)
            })
        }
    }
}