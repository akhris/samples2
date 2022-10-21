package ui.toolbar_utils.sampletypes_selector

import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import domain.SampleType
import ui.components.ListSelector

@Composable
fun SampleTypesSelectorUi(
    component: ISampleTypesSelector,
    onSampleTypeSelected: (SampleType?) -> Unit,
    onEditSampleTypeClick: (SampleType?) -> Unit
) {

    val state by remember(component) { component.state }.subscribeAsState()

    LaunchedEffect(state.selectedType) {
        onSampleTypeSelected(state.selectedType)
    }

    ListSelector(
        modifier = Modifier.width(320.dp),
        currentSelection = state.selectedType,
        items = state.types,
        onAddNewClicked = { onEditSampleTypeClick(null) },
        onItemEdit = {
            onEditSampleTypeClick(it)
        },
        onItemSelected = {
            component.selectType(it)
        },
        itemName = { it.name },
        itemDescription = { it.description },
        title = "Тип образцов"
    )

}