package ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ui.UiSettings

/**
 * Pagination controls like here:
 * https://material.io/components/data-tables#behavior
 */
@Composable
fun Pagination(
    modifier: Modifier = Modifier,
    rowsPerPage: Int = 25,
    onRowsPerPageChanged: (Int) -> Unit,
    currentPage: Int,
    onPageChanged: (Int) -> Unit,
    maxItemsCount: Long
) {

    val totalPages = remember(maxItemsCount, rowsPerPage) {
        (maxItemsCount / rowsPerPage).toInt() + 1
    }

    val firstRow = remember(rowsPerPage, currentPage) { (currentPage - 1) * rowsPerPage + 1 }
    val lastRow = remember(rowsPerPage, firstRow, maxItemsCount) {
        val a = firstRow + rowsPerPage - 1
        if (a > maxItemsCount)
            maxItemsCount.toInt()
        else a
    }

    Row(
        modifier = modifier.height(UiSettings.PaginationPanel.panelHeight).padding(vertical = 4.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
    ) {
        Text("Строк на странице")
        RowsPerPageControl(rowsPerPage, onRowsPerPageChanged)
        //Currently visible rows indication:
        Text(modifier = modifier.padding(horizontal = 16.dp), text = "$firstRow-$lastRow из $maxItemsCount")
        //Buttons:
        IconButton(onClick = {
            onPageChanged(1)
        }, enabled = currentPage > 1) {
            Icon(
                painter = painterResource("vector/first_page_black_24dp.svg"),
                contentDescription = "navigate to the first page"
            )
        }
        IconButton(onClick = { onPageChanged(currentPage - 1) }, enabled = currentPage > 1) {
            Icon(
                painter = painterResource("vector/navigate_before_black_24dp.svg"),
                contentDescription = "navigate to previous page"
            )
        }
        IconButton(onClick = { onPageChanged(currentPage + 1) }, enabled = currentPage < totalPages) {
            Icon(
                painter = painterResource("vector/navigate_next_black_24dp.svg"),
                contentDescription = "navigate to next page"
            )
        }
        IconButton(onClick = { onPageChanged(totalPages) }, enabled = currentPage < totalPages) {
            Icon(
                painter = painterResource("vector/last_page_black_24dp.svg"),
                contentDescription = "navigate to the last page"
            )
        }
    }
}

@Composable
private fun RowScope.RowsPerPageControl(rowsPerPage: Int, onRowsPerPageChanged: (Int) -> Unit) {
    var isMenuOpened by remember(rowsPerPage) { mutableStateOf(false) }

    val rotationAngle by animateFloatAsState(targetValue = if (isMenuOpened) 180f else 0f)
    Box(
        modifier = Modifier.width(UiSettings.PaginationPanel.rowsPerPageFieldWidth),
        contentAlignment = Alignment.CenterEnd
    ) {
        OutlinedTextField(
            value = rowsPerPage.toString(),
            onValueChange = {
                onRowsPerPageChanged(it.toIntOrNull() ?: 25)
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Rounded.ArrowDropDown,
                    modifier = Modifier.rotate(rotationAngle).clickable {
                        isMenuOpened = !isMenuOpened
                    },
                    contentDescription = "open menu"
                )
            }
        )


        DropdownMenu(
            expanded = isMenuOpened,
            onDismissRequest = { isMenuOpened = false }
        ) {
            for (i in 1..4) {
                val step = 25
                DropdownMenuItem(
                    modifier = Modifier.background(color = if (i * step == rowsPerPage) MaterialTheme.colors.primaryVariant else MaterialTheme.colors.surface),
                    onClick = {
                        onRowsPerPageChanged(i * step)
                    }) {
                    Text(
                        (i * step).toString()
                    )
                }
            }
        }
    }

}