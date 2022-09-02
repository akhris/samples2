package ui.screens.parameters

import LocalSamplesType
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import domain.Parameter
import ui.components.tables.DataTable
import ui.components.tables.mappers.ParametersDataMapper

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun ParametersUi(component: IParameters) {
    val state by remember(component) { component.state }.subscribeAsState()

    var showAddParameterDialog by remember { mutableStateOf(false) }

    val currentType = LocalSamplesType.current

//    val adapter = remember(state.parameters) {
//        ParametersAdapter(state.parameters,
//            onEntityChanged = {
//                component.updateParameter(it)
//            })
//    }

    val mapper = remember { ParametersDataMapper() }

    Box(modifier = Modifier.fillMaxSize()) {


        //parameters table:
        DataTable(
            modifier = Modifier.align(Alignment.TopCenter).padding(end = 80.dp),
            items = state.parameters, mapper = mapper, onItemChanged = { component.updateParameter(it) })

        FloatingActionButton(
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            onClick = { showAddParameterDialog = true },
            content = { Icon(Icons.Rounded.Add, contentDescription = "add parameter") })
    }


    if (showAddParameterDialog) {

        var paramName by remember { mutableStateOf("") }

        AlertDialog(
            modifier = Modifier.onKeyEvent {
                if (it.key == Key.Enter && (paramName.isNotEmpty() && currentType != null)) {
                    component.addNewParameter(Parameter(name = paramName, sampleType = currentType))
                    showAddParameterDialog = false

                    true
                } else {
                    false
                }
            },
            onDismissRequest = {
                showAddParameterDialog = false
            },
            text = {
                OutlinedTextField(
                    value = paramName,
                    onValueChange = { paramName = it },
                    singleLine = true
                )
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
                TextButton(onClick = { showAddParameterDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}