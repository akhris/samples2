package ui.screens.base_entity_screen.filter_dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Dialog
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import domain.FilterSpec
import domain.IEntity
import ui.components.ListMultiPickerDialog
import utils.log

@Composable
fun <T : IEntity> FilterEntityFieldUi(component: IFilterEntityFieldComponent<T>, onDismissDialog: () -> Unit) {

    val filterSpec by remember(component) { component.filterSpec }.subscribeAsState()
    val slice by remember(component) { component.slice }.subscribeAsState()



    ListMultiPickerDialog(
        items = slice,
        title = "Фильтры для ${filterSpec.columnName}",
        initialSelection = when (val spec = filterSpec) {
            is FilterSpec.Range<*> -> {
                listOf()
            }

            is FilterSpec.Values -> spec.filteredValues
        },
        onItemsPicked = {
            // TODO: add callback to [IEntityComponent.addFilter]
            log("picked items: ")
            it.forEach {
                log(it)
            }
        },
        onDismiss = onDismissDialog,
        isInverted = true
    )

//
//    Dialog(onCloseRequest = onDismissDialog, title = "Фильтры для ${filterSpec.columnName}") {
//        FilterEntityFieldContent(filterSpec, slice)
//    }
}

@Composable
private fun FilterEntityFieldContent(filterSpec: FilterSpec, slice: List<String>) {
    Text("filtering test")

    Column {
        slice.forEach {
            Text(it)
        }
    }
}