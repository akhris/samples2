package ui.components.tables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import domain.IEntity
import kotlinx.coroutines.delay
import ui.UiSettings

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun <T> DataTable(
    modifier: Modifier = Modifier,
    items: List<T>,
    mapper: IDataTableMapper<T>,
    onItemChanged: (T) -> Unit,
    isSelectable: Boolean = true
) {

    val selectionMap = remember { mutableStateMapOf<String, Boolean>() }

    //all items are selected:
    val checks = items
        .map { item -> selectionMap[mapper.getId(item)] ?: false }

    val checkState = remember(checks) {
        if (checks.all { it }) {
            ToggleableState.On
        } else if (checks.all { !it } || checks.isEmpty()) {
            ToggleableState.Off
        } else {
            ToggleableState.Indeterminate
        }
    }



    Surface(
        modifier = modifier.padding(start = 16.dp, top = 16.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        LazyColumn {

            stickyHeader {
                Row(
                    modifier = Modifier.height(UiSettings.DataTable.headerRowHeight),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    //render header:
                    //selection box:
                    if (isSelectable) {
                        TriStateCheckbox(state = checkState, onClick = {
                            when (checkState) {
                                ToggleableState.On -> selectionMap.clear()
                                ToggleableState.Off,
                                ToggleableState.Indeterminate -> {
                                    items.forEach {
                                        selectionMap[mapper.getId(it)] = true
                                    }
                                }
                            }
                        })
                    }

                    for (column in mapper.columns) {
                        Box(
                            modifier = Modifier
                                .width(UiSettings.DataTable.minCellWidth)
                                .padding(horizontal = UiSettings.DataTable.columnPadding)
                        ) {
                            Text(
                                modifier = Modifier.align(Alignment.CenterStart)
                                    .padding(all = UiSettings.DataTable.cellPadding),
                                text = column.title,
                                style = MaterialTheme.typography.subtitle2,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                    }
                }
            }

            this
                .items(items, key = {
                    mapper.getId(it)
                }) { item ->

                    var isHover by remember { mutableStateOf(false) }

                    //render cells row:

                    Row(
                        modifier = Modifier.height(UiSettings.DataTable.rowHeight)
                            .onPointerEvent(PointerEventType.Enter) { isHover = true }
                            .onPointerEvent(PointerEventType.Exit) { isHover = false }
                            .background(
                                color = if (isHover) {
                                    MaterialTheme.colors.primary.copy(alpha = 0.1f)
                                } else MaterialTheme.colors.surface
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isSelectable) {
                            Checkbox(checked = selectionMap[mapper.getId(item)] == true, onCheckedChange = {
                                selectionMap[mapper.getId(item)] = it
                            })
                        }
                        for (column in mapper.columns) {
                            //render cell:
                            Box(
                                modifier = Modifier.width(UiSettings.DataTable.minCellWidth)
                                    .padding(horizontal = UiSettings.DataTable.columnPadding)
                            ) {

                                RenderCell(
                                    modifier =
                                    Modifier
                                        .padding(all = UiSettings.DataTable.cellPadding),

                                    cell = mapper.getCell(item, column),
                                    onCellChanged = { changedCell ->
                                        onItemChanged(mapper.updateItem(item, column, changedCell))
                                    }
                                )
                            }
                        }
                    }


                }


        }
    }
}

@Composable
private fun BoxScope.RenderCell(modifier: Modifier = Modifier, cell: Cell, onCellChanged: (Cell) -> Unit) {
    when (cell) {
        is Cell.EditTextCell -> RenderEditTextCell(modifier, cell, onCellChanged)
        is Cell.EntityCell -> RenderEntityCell(modifier, cell, onCellChanged)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun BoxScope.RenderEditTextCell(
    modifier: Modifier = Modifier,
    cell: Cell.EditTextCell,
    onCellChanged: (Cell) -> Unit
) {

    var value by remember(cell) { mutableStateOf(cell.value) }
    var isHover by remember { mutableStateOf(false) }

    BasicTextField(
        modifier = modifier
            .onPointerEvent(PointerEventType.Enter) { isHover = true }
            .onPointerEvent(PointerEventType.Exit) { isHover = false }
            .background(color = MaterialTheme.colors.primary.copy(alpha = if (isHover) 0.05f else 0f)).padding(4.dp),
        value = value,
        onValueChange = { value = it },
        singleLine = true
    )


    //debounce:
    LaunchedEffect(value) {
        if (value == cell.value) {
            return@LaunchedEffect
        }
        delay(UiSettings.Debounce.debounceTime)
        onCellChanged(cell.copy(value = value))
    }
}


@Composable
private fun BoxScope.RenderEntityCell(
    modifier: Modifier = Modifier,
    cell: Cell.EntityCell,
    onCellChanged: (Cell) -> Unit
) {


    Text(text = cell.entityClass.name)

}


data class ColumnId(val key: String, val title: String)

interface IDataTableMapper<T> {
    val columns: List<ColumnId>
    fun getCell(item: T, columnId: ColumnId): Cell
    fun updateItem(item: T, columnId: ColumnId, cell: Cell): T
    fun getId(item: T): String
}

sealed class Cell {
    data class EditTextCell(val value: String) : Cell()
    data class EntityCell(val entity: IEntity?, val entityClass: Class<out IEntity>) : Cell()
//    data class ReferenceCell() : Cell()
}