package ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ColumnScopeInstance.align
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import domain.SampleType
import ui.UiSettings

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SampleTypeSelector(
    typesList: List<SampleType>,
    selectedType: SampleType,
    onSampleTypeSelected: (SampleType) -> Unit,
    onNewSampleTypeAdd: () -> Unit
) {

    var isMenuOpened by remember(selectedType) { mutableStateOf(false) }
    val rotation by animateFloatAsState(if (isMenuOpened) 180f else 0f)

    Box(modifier = Modifier.width(UiSettings.SampleTypesSelector.selectorWidth)) {
        ListItem(
            modifier = Modifier.fillMaxWidth(),
            text = {
                Text(text = selectedType.name)
            }, secondaryText = {
                Text(text = selectedType.description)
            }, trailing = {
                IconButton(onClick = {
                    isMenuOpened = !isMenuOpened
                }) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowDropDown,
                        modifier = Modifier.size(UiSettings.SampleTypesSelector.dropDownIconSize).rotate(rotation),
                        contentDescription = "open drop-down"
                    )
                }
            })

        if (isMenuOpened) {
            DropdownMenu(
                expanded = true,
                onDismissRequest = {
                    isMenuOpened = !isMenuOpened
                }
            ) {
                typesList.forEach { sampleType ->
                    DropdownMenuItem(onClick = {
                        onSampleTypeSelected(sampleType)
                    }) {
                        Text(sampleType.name)
                    }
                }
                DropdownMenuItem(onClick = onNewSampleTypeAdd) {
                    Icon(
                        Icons.Rounded.AddCircle,
                        modifier = Modifier.align(Alignment.Center),
                        contentDescription = "add new sample type"
                    )
                }
            }
        }
    }

}