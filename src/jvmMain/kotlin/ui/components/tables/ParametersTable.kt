package ui.components.tables

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import domain.Parameter

@Composable
fun BoxScope.ParametersTable(
    parameters: List<Parameter>
) {

    Table(
        modifier = Modifier.matchParentSize(),
        columnCount = 2,
        rowCount = parameters.size,
        cellContent = { columnIndex, rowIndex ->
            when(columnIndex){
                1 -> {
                    Text(text = parameters[rowIndex].name)
                }

                2 -> {
                    Text(text=parameters[rowIndex].description)
                }
            }
        },
    beforeRow = {
        Text(it.toString())
    })
//
//    BaseTable(
//        maxColumns = 2,
//        getHeader = { column ->
//            when (column) {
//                1 -> {
//                    Text(textAlign = TextAlign.Center, text = "имя")
//                }
//
//                2 -> {
//                    Text(text="описание")
//                }
//            }
//        },
//        getCell = { column, row ->
//            parameters
//                .getOrNull(row)
//                ?.let { p ->
//                    when (column) {
//                        1 -> {
//                            Text(text = p.name)
//                        }
//
//                        2 -> {
//                            Text(text = p.description)
//                        }
//                    }
//                }
//        },
//        totalItems = parameters.size.toLong(),
//        maxRowsOnPage = { page ->
//            5
//        }
//    )

}