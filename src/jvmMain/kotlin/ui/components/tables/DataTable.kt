package ui.components.tables

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import domain.IEntity
import kotlinx.coroutines.delay
import ui.UiSettings
import utils.DateTimeConverter
import java.time.LocalDateTime
import kotlin.reflect.KClass

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun <T> DataTable(
    modifier: Modifier = Modifier,
    items: List<T>,
    mapper: IDataTableMapper<T>,
    onItemChanged: ((T) -> Unit)? = null,
    selectionMode: SelectionMode<T> = SelectionMode.Multiple(),
    onCellClicked: ((T, Cell, ColumnId) -> Unit)? = null
) {

    val selectionMap = remember {
        mutableStateMapOf<String, Boolean>()
    }

    LaunchedEffect(selectionMode) {
        val initialSelection: Map<String, Boolean> = when (selectionMode) {
            is SelectionMode.Multiple<T> -> mapOf()
            SelectionMode.None -> mapOf()
            is SelectionMode.Single -> listOfNotNull(selectionMode.initialSelection?.let { it to true }).toMap()
        }
        selectionMap.putAll(initialSelection)
    }

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


    Box(modifier = modifier.fillMaxHeight()) {


        Surface(
            modifier = modifier.padding(start = 16.dp, top = 16.dp),
            shape = MaterialTheme.shapes.medium
        ) {

            val listState = rememberLazyListState()
            val headerElevation by animateDpAsState(if (listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0) 0.dp else 4.dp)
            LazyColumn(state = listState) {

                stickyHeader {
                    Surface(elevation = headerElevation) {
                        Row(
                            modifier = Modifier.height(UiSettings.DataTable.headerRowHeight),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            //render header:
                            //selection box:
                            Box(modifier = Modifier.width(UiSettings.DataTable.selectionRowWidth)) {
                                if (selectionMode is SelectionMode.Multiple) {
                                    TriStateCheckbox(state = checkState, onClick = {
                                        when (checkState) {
                                            ToggleableState.On -> {
                                                selectionMap.clear()
                                                selectionMode.onItemsSelected?.invoke(listOf())
//                                            onSelectionChanged?.invoke(listOf())
                                            }

                                            ToggleableState.Off,
                                            ToggleableState.Indeterminate -> {
                                                items.forEach {
                                                    selectionMap[mapper.getId(it)] = true
                                                }
                                                selectionMode.onItemsSelected?.invoke(
                                                    selectionMap.filterValues { it }.keys.mapNotNull { key ->
                                                        items.find {
                                                            mapper.getId(
                                                                it
                                                            ) == key
                                                        }
                                                    }
                                                )
                                            }
                                        }
                                    }, modifier = Modifier.align(Alignment.Center))
                                }
                            }

                            for (column in mapper.columns) {
                                Box(
                                    modifier = Modifier
                                        .width(
                                            when (column.width) {
                                                is ColumnWidth.Custom -> column.width.width
                                                ColumnWidth.Normal -> UiSettings.DataTable.columnWidthNormal
                                                ColumnWidth.Small -> UiSettings.DataTable.columnWidthSmall
                                                ColumnWidth.Wide -> UiSettings.DataTable.columnWidthWide
                                            }
                                        )
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
                }

                this
                    .items(items, key = {
                        mapper.getId(it)
                    }) { item ->

                        var isHover by remember { mutableStateOf(false) }

                        //render cells row:

                        Row(
                            modifier = Modifier
                                .height(UiSettings.DataTable.rowHeight)
                                .onPointerEvent(PointerEventType.Enter) { isHover = true }
                                .onPointerEvent(PointerEventType.Exit) { isHover = false }
                                .background(
                                    color = if (isHover) {
                                        MaterialTheme.colors.primary.copy(alpha = 0.1f)
                                    } else MaterialTheme.colors.surface
                                )
                                .clickable {
                                    when (selectionMode) {
                                        is SelectionMode.Multiple -> {
                                            selectionMap[mapper.getId(item)] =
                                                !(selectionMap[mapper.getId(item)] ?: false)
                                            selectionMode.onItemsSelected?.invoke(
                                                selectionMap.filterValues { it }.keys.mapNotNull { key ->
                                                    items.find {
                                                        mapper.getId(
                                                            it
                                                        ) == key
                                                    }
                                                }
                                            )
                                        }

                                        SelectionMode.None -> {}
                                        is SelectionMode.Single -> {
                                            val prevValue = selectionMap[mapper.getId(item)] ?: false
                                            selectionMap.clear()
                                            selectionMap[mapper.getId(item)] = !prevValue
                                            selectionMode.onItemSelected?.invoke(
                                                if (prevValue) null else item
                                            )
                                        }
                                    }
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // selection control:
                            Box(
                                modifier = Modifier.width(UiSettings.DataTable.selectionRowWidth),
                                contentAlignment = Alignment.Center
                            ) {
                                when (selectionMode) {
                                    is SelectionMode.Multiple -> {
                                        Checkbox(
                                            checked = selectionMap[mapper.getId(item)] == true,
                                            onCheckedChange = null
                                        )
                                    }

                                    is SelectionMode.Single -> {
                                        RadioButton(selected = selectionMap[mapper.getId(item)] == true, onClick = null)
                                    }
                                }
                            }
                            for (column in mapper.columns) {
                                //render cell:
                                val cell = remember(mapper, item, column) {
                                    mapper.getCell(item, column)
                                }

                                Box(
                                    modifier = Modifier.width(
                                        when (column.width) {
                                            is ColumnWidth.Custom -> column.width.width
                                            ColumnWidth.Normal -> UiSettings.DataTable.columnWidthNormal
                                            ColumnWidth.Small -> UiSettings.DataTable.columnWidthSmall
                                            ColumnWidth.Wide -> UiSettings.DataTable.columnWidthWide
                                        }
                                    )
                                        .padding(horizontal = UiSettings.DataTable.columnPadding)
                                        .clickable {
                                            onCellClicked?.invoke(item, cell, column)
                                        }
                                ) {

                                    RenderCell(
                                        modifier =
                                        Modifier
                                            .padding(all = UiSettings.DataTable.cellPadding),

                                        cell = cell,
                                        onCellChanged = { changedCell ->
                                            onItemChanged?.invoke(mapper.updateItem(item, column, changedCell))
                                        }
                                    )
                                }
                            }
                        }
                    }
            }
        }

        if (selectionMode is SelectionMode.Multiple<T> && selectionMap.any { it.value }) {
            //show control buttons:
            Surface(modifier = Modifier.align(Alignment.BottomCenter)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),

                    ) {
                    Button(onClick = {

                    }) {
                        Text(text = "Дублировать")
                    }
                    TextButton(onClick = {

                    }) {
                        Text(text = "Удалить", color = MaterialTheme.colors.error)
                        Icon(
                            Icons.Rounded.Delete,
                            contentDescription = "Удалить записи",
                            tint = MaterialTheme.colors.error
                        )
                    }
                }
            }
        }

    }
}

@Composable
private fun BoxScope.RenderCell(
    modifier: Modifier = Modifier,
    cell: Cell,
    onCellChanged: (Cell) -> Unit
) {
    when (cell) {
        is Cell.EditTextCell -> RenderEditTextCell(modifier, cell, onCellChanged)
        is Cell.EntityCell -> RenderEntityCell(modifier, cell, onCellChanged)
        is Cell.DateTimeCell -> RenderDateTimeCell(modifier, cell)
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun BoxScope.RenderDateTimeCell(
    modifier: Modifier = Modifier,
    cell: Cell.DateTimeCell
) {
    var isHover by remember { mutableStateOf(false) }

    Text(
        modifier = modifier
            .onPointerEvent(PointerEventType.Enter) { isHover = true }
            .onPointerEvent(PointerEventType.Exit) { isHover = false }
            .background(color = MaterialTheme.colors.primary.copy(alpha = if (isHover) 0.05f else 0f)).padding(4.dp),
        text = cell.value?.let { DateTimeConverter.dateTimeToString(it) } ?: ""
    )


}


@Composable
private fun BoxScope.RenderEntityCell(
    modifier: Modifier = Modifier,
    cell: Cell.EntityCell,
    onCellChanged: (Cell) -> Unit
) {
//    var showEntityPickerDialog by remember { mutableStateOf<KClass<out IEntity>?>(null) }
//    Text(text = cell.entityClass.simpleName ?: "", modifier = Modifier.clickable { onCellClicked(cell) })
    Text(text = cell.entity?.toString() ?: "")


}


data class ColumnId(val key: String, val title: String, val width: ColumnWidth = ColumnWidth.Normal)

sealed class ColumnWidth {
    object Small : ColumnWidth()
    object Normal : ColumnWidth()
    object Wide : ColumnWidth()
    class Custom(val width: Dp) : ColumnWidth()
}

interface IDataTableMapper<T> {
    val columns: List<ColumnId>
    fun getCell(item: T, columnId: ColumnId): Cell
    fun updateItem(item: T, columnId: ColumnId, cell: Cell): T
    fun getId(item: T): String
}

/**
 * Calculate table width based on columns width settings.
 */
fun IDataTableMapper<*>.getTableWidth(): Dp {
    return columns.fold(0.dp) { acc, columnId ->
        acc + when (columnId.width) {
            is ColumnWidth.Custom -> columnId.width.width
            ColumnWidth.Normal -> UiSettings.DataTable.columnWidthNormal
            ColumnWidth.Small -> UiSettings.DataTable.columnWidthSmall
            ColumnWidth.Wide -> UiSettings.DataTable.columnWidthWide
        }
    }
}

sealed class Cell {
    data class EditTextCell(val value: String) : Cell()
    data class EntityCell(val entity: IEntity?, val entityClass: KClass<out IEntity>) : Cell()

    data class DateTimeCell(val value: LocalDateTime?) : Cell()
//    data class ReferenceCell() : Cell()
}

sealed class SelectionMode<T> {
    data class Single<T>(
        val initialSelection: String? = null,
        val onItemSelected: ((T?) -> Unit)? = null
    ) :
        SelectionMode<T>()

    class Multiple<T>(
        val initialSelection: List<String> = listOf(),
        val onItemsSelected: ((List<T>) -> Unit)? = null
    ) :
        SelectionMode<T>()

    object None : SelectionMode<Any>()
}