package ui.components.tables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ui.UiSettings
import ui.components.ScrollableBox


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BaseTablePaginated(
//loadRow: (row: Int)->Row
) {

    var rowsPerPage by remember { mutableStateOf(10) }


    ScrollableBox(
        modifier = Modifier.fillMaxWidth(),
        innerHorizontalPadding = 80.dp,
        footer = {
            //todo pagination pane


        }
    ) {
        LazyColumn {

            stickyHeader {
                Row(modifier = Modifier.fillMaxWidth()) {
                    //render header:
                    for (column in 0 until columnCount) {
                        Box(
                            modifier = Modifier.weight(1f)
                                .border(
                                    color = UiSettings.DataTable.gridLinesColor,
                                    width = UiSettings.DataTable.gridLinesWidth
                                )
                        ) {
                            Text(modifier = Modifier.align(Alignment.Center), text = adapter.getHeader(column))
                        }

                    }
                }
            }

            this.items(rowsPerPage) {
                //loadRow(it)
            }
        }

    }

}