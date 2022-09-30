package ui.components.tables

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.mouseClickable
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import domain.IEntity
import domain.valueobjects.Factor
import domain.valueobjects.factors
import kotlinx.coroutines.delay
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorder
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import ui.UiSettings
import utils.DateTimeConverter
import utils.log
import java.time.LocalDateTime
import kotlin.math.max
import kotlin.math.min
import kotlin.reflect.KClass

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun <T> DataTable(
    modifier: Modifier = Modifier,
    items: List<T>,
    mapper: IDataTableMapper<T>,
    onItemChanged: ((T) -> Unit)? = null,
    selectionMode: SelectionMode<T> = SelectionMode.Multiple(),
    onCellClicked: ((T, Cell, ColumnId) -> Unit)? = null,
    onHeaderClicked: ((ColumnId) -> Unit)? = null,
    onSortingChanged: ((column: ColumnId, isAsc: Boolean) -> Unit)? = null,
    footer: (@Composable () -> Unit)? = null,
    firstItemIndex: Int? = null,
    onReorder: ((from: T, to: T) -> Unit)? = null
) {



    val selectionMap = remember {
        mutableStateMapOf<String, Boolean>()
    }

    LaunchedEffect(selectionMode) {
        val initialSelection: Map<String, Boolean> = when (selectionMode) {
            is SelectionMode.Multiple<T> -> mapOf()
            is SelectionMode.None -> mapOf()
            is SelectionMode.Single -> listOfNotNull(selectionMode.initialSelection?.let { it to true }).toMap()
        }
        selectionMap.putAll(initialSelection)
    }

    //all items are selected:
    val checks = items
        .map { item -> selectionMap[mapper.getId(item)] ?: false }

    val checkState = remember(checks) {
        if (checks.all { it } && checks.isNotEmpty()) {
            ToggleableState.On
        } else if (checks.all { !it } || checks.isEmpty()) {
            ToggleableState.Off
        } else {
            ToggleableState.Indeterminate
        }
    }

    val tableWidth = remember(mapper) { mapper.getTableWidth() }
//    var windowWidth by remember { mutableStateOf(1.dp) }
//    Surface(
//        modifier = modifier.padding(start = 16.dp, top = 16.dp),
//        shape = MaterialTheme.shapes.medium
//    ) {

    val listState = rememberReorderableLazyListState(onMove = { from, to ->
//        _items.value = _items.value.toMutableList().apply {
//            add(max(to.index - 1, 0), removeAt(max(from.index - 1, 0)))
//        }
        onReorder?.invoke(items[max(from.index - 1, 0)], items[max(to.index - 1, 0)])
    }, canDragOver = { it.index > 0 && it.index <= items.size })
    val headerElevation by animateDpAsState(if (listState.listState.firstVisibleItemIndex == 0 && listState.listState.firstVisibleItemScrollOffset == 0) 0.dp else 4.dp)

    val selectItem = remember {
        { item: T ->
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

                is SelectionMode.None -> {}
                is SelectionMode.Single -> {
                    val prevValue = selectionMap[mapper.getId(item)] ?: false
                    selectionMap.clear()
                    selectionMap[mapper.getId(item)] = !prevValue
                    selectionMode.onItemSelected?.invoke(
                        if (prevValue) null else item
                    )
                }
            }
        }
    }

//        log("rendering table:\nmap: $selectionMap\nchecks: $checks\ncheckState: $checkState\nheaderElevation: $headerElevation")

    var lastClickedIndex by remember(items) { mutableStateOf(-1) }


    LazyColumn(
        modifier =
        modifier
            .reorderable(listState)
            .detectReorder(listState)
//            .onSizeChanged {
//                windowWidth = it.width.dp
//            }
//            .scale(if (windowWidth > 1.dp) tableWidth / windowWidth else 1f)
        ,
        state = listState.listState
    ) {

        stickyHeader {

            Row(
                modifier = Modifier.height(UiSettings.DataTable.headerRowHeight),
                verticalAlignment = Alignment.CenterVertically
            ) {
                //render header:

                //space for first item index if used:
                firstItemIndex?.let {
                    Spacer(modifier = Modifier.width(UiSettings.DataTable.additionalRowWidth))
                }
                Surface(elevation = headerElevation) {
                    Row(
                        modifier = Modifier.height(
                            UiSettings.DataTable.headerRowHeight
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        //selection box:
                        Box(modifier = Modifier.width(UiSettings.DataTable.additionalRowWidth)) {
                            if (selectionMode is SelectionMode.Multiple && items.size > 1) {
                                TriStateCheckbox(state = checkState, onClick = {
                                    when (checkState) {
                                        ToggleableState.On -> {
                                            selectionMap.clear()
                                            selectionMode.onItemsSelected?.invoke(listOf())
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

                        var sorting by remember { mutableStateOf<Pair<ColumnId, Boolean>?>(null) }


                        for (column in mapper.columns) {

                            Row(
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
                                    .clickable {
                                        onHeaderClicked?.invoke(column)
                                        if (onSortingChanged != null) {
                                            sorting = column to !(sorting?.second ?: false)
                                            sorting?.let {
                                                onSortingChanged(it.first, it.second)
                                            }
                                        }
                                    }, verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(
                                    space = 2.dp, alignment = when (column.alignment) {
                                        ColumnAlignment.Center -> Alignment.CenterHorizontally
                                        ColumnAlignment.End -> Alignment.End
                                        ColumnAlignment.Start -> Alignment.Start
                                    }
                                )
                            ) {
                                Text(
                                    text = column.title,
                                    style = MaterialTheme.typography.subtitle2,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (column.secondaryText.isNotEmpty()) {
                                    Text(
                                        text = column.secondaryText,
                                        style = MaterialTheme.typography.caption.copy(
                                            color = MaterialTheme.colors.primaryVariant
                                        ),
//                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                //sorting icon:
                                if (onSortingChanged != null && sorting?.first == column) {
                                    IconButton(onClick = {
                                        sorting = column to !(sorting?.second ?: false)
                                        sorting?.let {
                                            onSortingChanged(it.first, it.second)
                                        }
                                    }) {
                                        Icon(
                                            Icons.Rounded.ArrowDropDown,
                                            "sort items",
                                            tint = MaterialTheme.colors.secondary,
                                            modifier = Modifier.rotate(if (sorting?.second == true) 180f else 0f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


        this
            .items(items = items, key = { item ->
                mapper.getId(item)
            }) { item ->
                ReorderableItem(listState, key = mapper.getId(item)) { isDragging ->
                    val elevation by animateDpAsState(if (isDragging) 16.dp else 0.dp)

                    val index = remember(item) { items.indexOf(item) }
                    var isHover by remember { mutableStateOf(false) }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        //if indexes are used:
                        firstItemIndex?.let { fii ->
                            Text(
                                modifier = Modifier.width(UiSettings.DataTable.additionalRowWidth),
                                text = (fii + index).toString(),
                                style = MaterialTheme.typography.caption.copy(
                                    color = UiSettings.DataTable.dividerColor(),
                                    textAlign = TextAlign.Center
                                )
                            )
                        }

                        //render cells row:
                        Surface(elevation = elevation) {
                            Box {
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
                                        .mouseClickable {
                                            selectItem(item)
                                            if (lastClickedIndex != -1 && keyboardModifiers.isShiftPressed) {
                                                for (i in lastClickedIndex + 1 until index) {
                                                    items.getOrNull(i)?.let {
                                                        selectItem(it)
                                                    }
                                                }
                                            }
                                            lastClickedIndex = index
                                        },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {


                                    // selection control:
                                    Box(
                                        modifier = Modifier.width(UiSettings.DataTable.additionalRowWidth),
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
                                                RadioButton(
                                                    selected = selectionMap[mapper.getId(item)] == true,
                                                    onClick = null
                                                )
                                            }

                                            is SelectionMode.None -> {

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
                                                },
                                            contentAlignment = when (column.alignment) {
                                                ColumnAlignment.Center -> Alignment.Center
                                                ColumnAlignment.End -> Alignment.CenterEnd
                                                ColumnAlignment.Start -> Alignment.CenterStart
                                            }
                                        ) {

                                            RenderCell(
                                                modifier =
                                                Modifier
                                                    .padding(all = UiSettings.DataTable.cellPadding),
                                                cell = cell,
                                                onCellChanged = { changedCell ->
                                                    onItemChanged?.invoke(mapper.updateItem(item, column, changedCell))
                                                },
                                                columnAlignment = column.alignment
                                            )
                                        }
                                    }
                                }
                                //divider:
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopCenter)
                                        .height(1.dp)
                                        .width(tableWidth + UiSettings.DataTable.additionalRowWidth)
                                        .background(color = UiSettings.DataTable.dividerColor())
                                )
                            }
                        }
                    }

                }
            }


        //footer
        item {
            footer?.invoke()
        }
    }
}


@Composable
private fun BoxScope.RenderCell(
    modifier: Modifier = Modifier,
    cell: Cell,
    onCellChanged: (Cell) -> Unit,
    columnAlignment: ColumnAlignment
) {
    when (cell) {
        is Cell.EditTextCell -> RenderEditTextCell(modifier, cell, onCellChanged, columnAlignment)
        is Cell.EntityCell -> RenderEntityCell(modifier, cell, onCellChanged, columnAlignment)
        is Cell.DateTimeCell -> RenderDateTimeCell(modifier, cell, columnAlignment)
        is Cell.BooleanCell -> RenderBooleanCell(modifier, cell, onCellChanged, columnAlignment)
        is Cell.ListCell -> TODO()
    }
}

@Composable
private fun BoxScope.RenderEditTextCell(
    modifier: Modifier = Modifier,
    cell: Cell.EditTextCell,
    onCellChanged: (Cell) -> Unit,
    columnAlignment: ColumnAlignment
) {

    var value by remember(cell) { mutableStateOf(cell.value) }

    BasicTextField(
        modifier = modifier,
        value = value,
        onValueChange = { value = it },
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(
            textAlign = when (columnAlignment) {
                ColumnAlignment.Center -> TextAlign.Center
                ColumnAlignment.End -> TextAlign.End
                ColumnAlignment.Start -> TextAlign.Start
            }
        )
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
private fun BoxScope.RenderBooleanCell(
    modifier: Modifier = Modifier,
    cell: Cell.BooleanCell,
    onCellChanged: (Cell) -> Unit,
    columnAlignment: ColumnAlignment
) {
    var value by remember(cell) { mutableStateOf(cell.value) }

    Switch(checked = value, onCheckedChange = { value = it })

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
private fun BoxScope.RenderDateTimeCell(
    modifier: Modifier = Modifier,
    cell: Cell.DateTimeCell,
    columnAlignment: ColumnAlignment
) {


    Text(
        modifier = modifier,
        text = cell.value?.let { DateTimeConverter.dateTimeToString(it) } ?: "",
        style = LocalTextStyle.current.copy(
            textAlign = when (columnAlignment) {
                ColumnAlignment.Center -> TextAlign.Center
                ColumnAlignment.End -> TextAlign.End
                ColumnAlignment.Start -> TextAlign.Start
            }
        )
    )


}


@Composable
private fun BoxScope.RenderEntityCell(
    modifier: Modifier = Modifier,
    cell: Cell.EntityCell,
    onCellChanged: (Cell) -> Unit,
    columnAlignment: ColumnAlignment
) {

    when (cell.entityClass) {
        domain.Unit::class -> {
            RenderUnitCell(
                modifier = modifier,
                cell = cell,
                unit = cell.entity as? domain.Unit,
                factor = cell.tag as? Factor,
                onFactorChanged = { onCellChanged(cell.copy(tag = it)) },
                columnAlignment = columnAlignment
            )
        }

        else -> {
            Text(
                modifier = modifier, text = cell.entity?.toString() ?: "",
                style = LocalTextStyle.current.copy(
                    textAlign = when (columnAlignment) {
                        ColumnAlignment.Center -> TextAlign.Center
                        ColumnAlignment.End -> TextAlign.End
                        ColumnAlignment.Start -> TextAlign.Start
                    }
                )
            )
        }
    }

}

@Composable
private fun BoxScope.RenderUnitCell(
    modifier: Modifier = Modifier,
    cell: Cell.EntityCell,
    unit: domain.Unit?,
    factor: Factor?,
    onFactorChanged: (Factor) -> Unit,
    columnAlignment: ColumnAlignment
) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        if (unit?.isMultipliable == true) {
            var showFactorsList by remember { mutableStateOf(false) }

            Text(
                text = (factor ?: Factor.NoFactor)
                    .prefix,
                modifier =
                Modifier

                    .clickable {
                        //show factors list to choose from:
                        showFactorsList = true
                    }.padding(horizontal = 2.dp),
                color = MaterialTheme.colors.secondary
            )

            DropdownMenu(
                expanded = showFactorsList,
                onDismissRequest = { showFactorsList = false }
            ) {
                factors
                    .forEach { f ->
                        DropdownMenuItem(
                            modifier = Modifier.background(
                                if (f == factor) MaterialTheme.colors.secondary.copy(alpha = 0.5f) else MaterialTheme.colors.surface
                            ),
                            onClick = {
                                onFactorChanged(f)
                                showFactorsList = false
                            }) {
                            Text(
                                text = "${f.prefix}: ${f.name}"

                            )
                        }
                    }
            }

        }
        Text(
            modifier = modifier, text = cell.entity?.toString() ?: "",
            style = LocalTextStyle.current.copy(
                textAlign = when (columnAlignment) {
                    ColumnAlignment.Center -> TextAlign.Center
                    ColumnAlignment.End -> TextAlign.End
                    ColumnAlignment.Start -> TextAlign.Start
                }
            )
        )
    }
}


data class ColumnId(
    val key: String,    //might correspond to exposed column name for working filtering, sorting and grouping
    val title: String,
    val secondaryText: String = "",
    val width: ColumnWidth = ColumnWidth.Normal,
    val alignment: ColumnAlignment = ColumnAlignment.Start
)

sealed class ColumnWidth {
    object Small : ColumnWidth()
    object Normal : ColumnWidth()
    object Wide : ColumnWidth()
    class Custom(val width: Dp) : ColumnWidth()
}

sealed class ColumnAlignment {
    object End : ColumnAlignment()
    object Start : ColumnAlignment()
    object Center : ColumnAlignment()
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
    data class EntityCell(
        val entity: IEntity?,
        val entityClass: KClass<out IEntity>,
        val tag: Any? = null
    ) : Cell()

    data class DateTimeCell(val value: LocalDateTime?) : Cell()
    data class BooleanCell(val value: Boolean) : Cell()

    data class ListCell(val values: List<String>) : Cell()


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

    class None<T> : SelectionMode<T>()
}