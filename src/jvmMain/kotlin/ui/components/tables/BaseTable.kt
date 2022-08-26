package ui.components.tables

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*

import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import ui.UiSettings
import ui.components.ScrollableBox
import utils.log

@Composable
fun BaseTable(
    adapter: ITableAdapter
) {
//    var currentPage by remember { mutableStateOf(1) }

    val totalRows = remember(adapter) { adapter.getTotalRows() }
    val columnCount = remember(adapter) { adapter.getColumnCount() }
    val withHeader = remember(adapter) { adapter.withHeader() }

    ScrollableBox(
        modifier = Modifier.fillMaxWidth(),
        innerHorizontalPadding = 80.dp,
        header = if (withHeader) {
            {
                Row(modifier = Modifier.fillMaxWidth()) {
                    //render header:
                    for (column in 0 until columnCount) {
                        Box(
                            modifier = Modifier.weight(1f)
                                .border(color = UiSettings.DataTable.gridLinesColor, width = UiSettings.DataTable.gridLinesWidth)
                        ) {
                            Text(modifier = Modifier.align(Alignment.Center), text = adapter.getHeader(column))
                        }

                    }
                }
            }
        } else null
    ) {
//        Column {


            Column {
                //render rows:
                for (row in 0 until totalRows) {
                    Row {
                        for (column in 0 until columnCount) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .border(
                                        color = UiSettings.DataTable.gridLinesColor,
                                        width = UiSettings.DataTable.gridLinesWidth
                                    )
                                    .padding(UiSettings.DataTable.cellPadding)
                            ) {

                                val initialValue =
                                    remember(adapter, column, row) { adapter.getCellValue(column, row) }

                                var cellValue by remember(initialValue) { mutableStateOf(initialValue) }

                                TextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    value = cellValue,
                                    onValueChange = { cellValue = it })

                                //debounce logic:
                                LaunchedEffect(cellValue) {
                                    if (cellValue == initialValue) {
                                        return@LaunchedEffect
                                    }
                                    delay(UiSettings.Debounce.debounceTime)
                                    log("changing value in adapter:")
                                    adapter.setCellValue(column, row, cellValue)
                                }
                            }
                        }
                    }
                }

            }
        }
//    }

}