package ui.components.tables

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun BaseTable(
    maxColumns: Int,
    getHeader: (@Composable ColumnScope.(column: Int) -> Unit)? = null,
    getCell: @Composable (x: Int, y: Int) -> Unit,
    maxRowsOnPage: (page: Int) -> Int = { 20 },
    totalItems: Long
) {
    var currentPage by remember { mutableStateOf(1) }

    Column(modifier = Modifier.fillMaxWidth()) {
        getHeader?.let { getH ->
            Row(modifier = Modifier.fillMaxWidth()) {
                //render header:
                for (column in 1..maxColumns) {
                    Box(modifier = Modifier.weight(1f))
                    this@Column.getH(column)
                }
            }
        }

        //render rows:
        for (row in 0..maxRowsOnPage(currentPage)) {
            Row {
                for (column in 1..maxColumns) {
                    Box(modifier = Modifier.weight(1f).border(width = 1.dp, color = Color.Gray).padding(8.dp)) {
                        getCell.invoke(column, row)
                    }
                }
            }
        }

    }
}