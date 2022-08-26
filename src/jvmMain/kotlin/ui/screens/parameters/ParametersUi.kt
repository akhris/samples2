package ui.screens.parameters

import LocalSamplesType
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import domain.Parameter
import ui.components.tables.BaseTable
import ui.components.tables.ParametersAdapter

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ParametersUi(component: IParameters) {
    val state by remember(component) { component.state }.subscribeAsState()

    var showAddParameterDialog by remember { mutableStateOf(false) }

    val currentType = LocalSamplesType.current

    val adapter = remember(state.parameters) {
        ParametersAdapter(state.parameters,
            onEntityChanged = {
                component.updateParameter(it)
            })
    }

    Box(modifier = Modifier.fillMaxSize()) {


        //parameters table:
        BaseTable(adapter)

        FloatingActionButton(
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            onClick = { showAddParameterDialog = true },
            content = { Icon(Icons.Rounded.Add, contentDescription = "add parameter") })
    }


    if (showAddParameterDialog) {

        var paramName by remember { mutableStateOf("") }

        AlertDialog(onDismissRequest = {
            showAddParameterDialog = false
        },
            text = {
                BasicTextField(value = paramName, onValueChange = { paramName = it })
            },
            confirmButton = {
                Button(
                    enabled = paramName.isNotEmpty() && currentType != null,
                    onClick = {
                        currentType?.let { st ->
                            component.addNewParameter(Parameter(name = paramName, sampleType = st))
                            showAddParameterDialog = false
                        }
                    }
                ) {
                    Text(text = "Добавить")
                }
            }, dismissButton = {
                Button(onClick = { showAddParameterDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}