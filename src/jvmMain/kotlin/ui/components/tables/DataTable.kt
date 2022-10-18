package ui.components.tables

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.compose.ui.zIndex
import domain.IEntity
import domain.valueobjects.Factor
import domain.valueobjects.factors
import kotlinx.coroutines.delay
import org.jetbrains.skiko.Cursor
import ui.UiSettings
import ui.components.DataTableEditTextField
import utils.*
import java.time.LocalDateTime
import kotlin.reflect.KClass

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> DataTable(
    modifier: Modifier = Modifier,
    items: List<T>,
    selection: List<String> = listOf(),
    onSelectionChanged: (items: List<T>, areSelected: Boolean) -> Unit = { _, _ -> },
    selectionMode: SelectionMode? = null,
    mapper: IDataTableMapper<T>,
    onItemChanged: ((T) -> Unit)? = null,
    onCellClicked: ((T, Cell, ColumnId) -> Unit)? = null,
    onItemRowClicked: ((T) -> Unit)? = null,
    utilitiesPanel: @Composable() (BoxScope.() -> Unit)? = null,
    footer: @Composable() (() -> Unit)? = null,
    headerMenu: @Composable() (ColumnScope.(column: ColumnId) -> Unit)? = null,
    headerStateIcons: @Composable() (RowScope.(column: ColumnId) -> Unit)? = null,
    firstItemIndex: Int? = null,
    isReorderable: Boolean = false
) {

    val _items = remember(items) { items.toMutableStateList() }

    val indicators = remember {
        mutableStateMapOf<String, OperationIndicator>()
    }

    val columnWidths = remember { mutableStateMapOf<ColumnId, Dp>() }


    val listState = rememberLazyListState()

    val tableWidth = remember(mapper, columnWidths.values.toList()) {
        mapper.columns.fold(0.dp) { sum, acc ->
            sum + (columnWidths[acc] ?: when (acc.width) {
                is ColumnWidth.Custom -> acc.width.width
                ColumnWidth.Normal -> UiSettings.DataTable.columnDefaultWidthNormal
                ColumnWidth.Small -> UiSettings.DataTable.columnDefaultWidthSmall
                ColumnWidth.Wide -> UiSettings.DataTable.columnDefaultWidthWide
            })
        }
    }

    val headerElevation by animateDpAsState(if (listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0) 0.dp else 4.dp)

    var isShiftPressed by remember { mutableStateOf(false) }
    var lastClickedIndex by remember(items) { mutableStateOf(-1) }

    Box {
        LazyColumn(
            modifier = modifier
                .onKeyEvent {
                    isShiftPressed = it.isShiftPressed
                    true
                },
            state = listState
        ) {
            stickyHeader {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    //render header:
                    //space for first item index if used:
                    firstItemIndex?.let {
                        Spacer(modifier = Modifier.width(UiSettings.DataTable.additionalRowWidth))
                    }
                    Surface(
                        elevation = headerElevation,
                        shape = RoundedCornerShape(
                            topStart = UiSettings.DataTable.cornerRadius,
                            topEnd = UiSettings.DataTable.cornerRadius
                        )
                    ) {
                        Column {
                            //additional panel (for filtering and other utilities)

                            utilitiesPanel?.let { up ->
                                Box(modifier = Modifier.fillMaxWidth().height(UiSettings.DataTable.headerRowHeight)) {
                                    up()
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomStart)
                                            .height(UiSettings.DataTable.dividerHeight)
                                            .width(tableWidth + UiSettings.DataTable.additionalRowWidth)
                                            .background(color = UiSettings.DataTable.dividerColor())
                                    )
                                }
                            }

                            //table header:
                            Row(
                                modifier = Modifier.height(UiSettings.DataTable.headerRowHeight),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                //selection box:
                                Box(modifier = Modifier.width(UiSettings.DataTable.additionalRowWidth)) {
                                    if (selectionMode == SelectionMode.Multiple && items.size > 1) {

                                        //all items are selected:
                                        val checks = items
                                            .map { item -> mapper.getId(item) in selection }

                                        val checkState = remember(checks) {
                                            if (checks.all { it } && checks.isNotEmpty()) {
                                                ToggleableState.On
                                            } else if (checks.all { !it } || checks.isEmpty()) {
                                                ToggleableState.Off
                                            } else {
                                                ToggleableState.Indeterminate
                                            }
                                        }


                                        TriStateCheckbox(state = checkState, onClick = {
                                            when (checkState) {
                                                ToggleableState.On -> {
                                                    onSelectionChanged(items, false)
                                                }

                                                ToggleableState.Off,
                                                ToggleableState.Indeterminate -> {
                                                    onSelectionChanged(items, true)
                                                }
                                            }
                                        }, modifier = Modifier.align(Alignment.Center))
                                    }
                                }

                                for (column in mapper.columns) {
                                    Box(
                                        modifier = Modifier
                                            .width(
                                                columnWidths[column] ?: when (column.width) {
                                                    is ColumnWidth.Custom -> column.width.width
                                                    ColumnWidth.Normal -> UiSettings.DataTable.columnDefaultWidthNormal
                                                    ColumnWidth.Small -> UiSettings.DataTable.columnDefaultWidthSmall
                                                    ColumnWidth.Wide -> UiSettings.DataTable.columnDefaultWidthWide
                                                }
                                            )
                                            .fillMaxHeight()
                                            .padding(horizontal = UiSettings.DataTable.columnPadding)
                                    ) {
                                        var showMenu by remember(column) { mutableStateOf(false) }

                                        CompositionLocalProvider(
                                            LocalLayoutDirection provides when (column.alignment) {
                                                ColumnAlignment.Start,
                                                ColumnAlignment.Center -> LayoutDirection.Ltr

                                                ColumnAlignment.End -> LayoutDirection.Rtl
                                            }
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .clickable {
                                                        if (headerMenu != null) {
                                                            showMenu = true
                                                        }
                                                    },
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                if (LocalLayoutDirection.current == LayoutDirection.Rtl)
                                                    Spacer(modifier = Modifier.width(UiSettings.DataTable.draggableAreaWidth))
                                                //primary title
                                                Row(
                                                    modifier = Modifier.weight(1f),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = column.title,
                                                        style = MaterialTheme.typography.subtitle2,
                                                        fontWeight = FontWeight.Bold,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                    //secondary title
                                                    if (column.secondaryText.isNotEmpty()) {
                                                        Text(
                                                            text = column.secondaryText,
                                                            style = MaterialTheme.typography.caption,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis
                                                        )
                                                    }
                                                }

                                                headerStateIcons?.let { hsi ->
                                                    hsi(column)
                                                    if (LocalLayoutDirection.current == LayoutDirection.Ltr)
                                                        Spacer(modifier = Modifier.width(UiSettings.DataTable.draggableAreaWidth))
                                                }


                                            }

                                        }
                                        //column resize area:
                                        Box(
                                            modifier =
                                            Modifier
                                                .align(Alignment.CenterEnd)
                                                .fillMaxHeight()
                                                .width(UiSettings.DataTable.draggableAreaWidth)
                                                .pointerHoverIcon(PointerIcon(Cursor(Cursor.E_RESIZE_CURSOR)))
                                                .combinedClickable(onDoubleClick = {
                                                    // reset to default width:
                                                    columnWidths.remove(column)
                                                }, onClick = {})
                                                .draggable(
                                                    state = rememberDraggableState { onDelta ->
                                                        columnWidths[column] =
                                                            (columnWidths[column] ?: when (column.width) {
                                                                is ColumnWidth.Custom -> column.width.width
                                                                ColumnWidth.Normal -> UiSettings.DataTable.columnDefaultWidthNormal
                                                                ColumnWidth.Small -> UiSettings.DataTable.columnDefaultWidthSmall
                                                                ColumnWidth.Wide -> UiSettings.DataTable.columnDefaultWidthWide
                                                            }) + onDelta.dp
                                                    },
                                                    orientation = Orientation.Horizontal
                                                )
                                        )


                                        if (showMenu)
                                            headerMenu?.let { hm ->
                                                DropdownMenu(
                                                    modifier = Modifier.align(Alignment.BottomEnd),
                                                    expanded = showMenu,
                                                    onDismissRequest = { showMenu = false },
                                                    content = { hm(column) }
                                                )
                                            }

                                    }
                                }
                            }
                        }
                    }
                }
            }
            itemsIndexed(_items, key = { index, item -> mapper.getId(item) }) { index, item ->
                RenderRow(
                    modifier = Modifier
                        .onFocusChanged {
                            if (it.hasFocus && selectionMode == SelectionMode.Single && (mapper.getId(item) !in selection)) {
                                onSelectionChanged(items, false)
                                onSelectionChanged(listOf(item), true)
                            }
                        }
                        .animateItemPlacement(),
                    renderIndex = {
                        firstItemIndex?.let { fii ->
                            Text(
                                modifier = Modifier.width(UiSettings.DataTable.additionalRowWidth),
                                text = (fii + index).toString(),
                                style = MaterialTheme.typography.caption,
                                textAlign = TextAlign.Center
                            )
                        }
                    },
                    renderIndicator = indicators[mapper.getId(item)]?.let { indicator ->
                        {
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .background(
                                        color = when (indicator) {
                                            OperationIndicator.ChangedItem -> Color.Yellow
                                            OperationIndicator.UpdatedSuccessfully -> Color.Green
                                            OperationIndicator.UpdatedWithError -> Color.Red
                                        }
                                    )
                            )
                        }

                    },
                    renderColumns = {
                        for (column in mapper.columns) {
                            //render cell:
                            val cell = remember(mapper, item, column) {
                                mapper.getCell(item, column)
                            }

                            Box(
                                modifier = Modifier
                                    .width(
                                        columnWidths[column] ?: when (column.width) {
                                            is ColumnWidth.Custom -> column.width.width
                                            ColumnWidth.Normal -> UiSettings.DataTable.columnDefaultWidthNormal
                                            ColumnWidth.Small -> UiSettings.DataTable.columnDefaultWidthSmall
                                            ColumnWidth.Wide -> UiSettings.DataTable.columnDefaultWidthWide
                                        }
                                    )
                                    .padding(horizontal = UiSettings.DataTable.columnPadding)
                                    .clickable(enabled = onCellClicked != null) {
                                        onCellClicked?.invoke(item, cell, column)
                                    },
                                contentAlignment = when (column.alignment) {
                                    ColumnAlignment.Center -> Alignment.Center
                                    ColumnAlignment.End -> Alignment.CenterEnd
                                    ColumnAlignment.Start -> Alignment.CenterStart
                                }
                            ) {
                                CompositionLocalProvider(
                                    LocalLayoutDirection provides when (column.alignment) {
                                        ColumnAlignment.Start,
                                        ColumnAlignment.Center -> LayoutDirection.Ltr

                                        ColumnAlignment.End -> LayoutDirection.Rtl
                                    }
                                ) {
                                    RenderCell(
                                        modifier =
                                        Modifier.padding(all = UiSettings.DataTable.cellPadding),
                                        cell = cell,
                                        columnId = column,
                                        onCellChanged = { changedCell ->
                                            _items.replace(
                                                mapper.updateItem(
                                                    item = item,
                                                    columnId = column,
                                                    cell = changedCell
                                                )
                                            ) {
                                                mapper.getId(it) == mapper.getId(item)
                                            }
//                                    onItemChanged?.invoke(mapper.updateItem(item, column, changedCell))
                                        },
                                        columnAlignment = column.alignment
                                    )
                                }
                            }
                        }
                    },
                    renderSelectionControl =
                    when (selectionMode) {
                        SelectionMode.Multiple -> {
                            {
                                Checkbox(
                                    checked = mapper.getId(item) in selection,
                                    onCheckedChange = {
                                        if (lastClickedIndex != -1 && isShiftPressed) {
                                            onSelectionChanged(items.subList(lastClickedIndex, index - 1), it)
                                        } else {
                                            onSelectionChanged(listOf(item), it)
                                        }
                                        lastClickedIndex = index
                                    }
                                )
                            }
                        }

                        SelectionMode.Single -> {
                            {
                                RadioButton(
                                    selected = mapper.getId(item) in selection,
                                    onClick = {
                                        val wasSelected = mapper.getId(item) in selection
                                        onSelectionChanged(items, false)
                                        onSelectionChanged(listOf(item), !wasSelected)
                                    }
                                )
                            }
                        }

                        else -> null
                    },
                    renderDragHandle = if (isReorderable) {
                        {
                            Column(
                                modifier = Modifier
                                    .width(UiSettings.DataTable.additionalRowWidth)
                                    .height(UiSettings.DataTable.rowHeight),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(modifier = Modifier.weight(1f)) {
                                    if (index > 0) {
                                        Icon(
                                            modifier = Modifier
                                                .align(Alignment.Center)
                                                .rotate(180f)
                                                .clickable { _items.moveUp(index) },
                                            imageVector = Icons.Rounded.ArrowDropDown,
                                            contentDescription = "move up",
                                            tint = MaterialTheme.colors.secondary
                                        )
                                    }
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    if (index < _items.size - 1)
                                        Icon(
                                            modifier = Modifier
                                                .align(Alignment.Center)
                                                .clickable { _items.moveDown(index) },
                                            imageVector = Icons.Rounded.ArrowDropDown,
                                            contentDescription = "move down",
                                            tint = MaterialTheme.colors.secondary
                                        )
                                }
                            }
                        }
                    } else null,
                    onRowClicked = {
                        if (selectionMode == SelectionMode.Single) {
                            val wasSelected = mapper.getId(item) in selection
                            onSelectionChanged(items, false)
                            onSelectionChanged(listOf(item), !wasSelected)
                        } else {
                            onItemRowClicked?.invoke(item)
                        }
                    },
                    tableWidth = tableWidth,
                    isLastRow = index == _items.size - 1
                )
            }

            footer?.let { f -> item { f() } }
        }
        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd),
            adapter = rememberScrollbarAdapter(
                scrollState = listState
            )
        )
        HorizontalScrollbar(
            modifier = Modifier.align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(end = 12.dp),
            adapter = rememberScrollbarAdapter(scrollState = listState)
        )
    }

    // debounce on items changing:
    LaunchedEffect(_items.toList(), items) {
        if (_items.toList() == items) {
            // if lists are equal - do not write changes to db
            indicators.clear()
            return@LaunchedEffect
        }

        //compare items one by one:
        val changedItems =
            _items
                .mapIndexed { index, t ->
                    mapper.updatePosition(t, index)
                }
                .filterIndexed { index, t ->
                    items.getOrNull(index) != t
                }

        changedItems.forEach {
            indicators[mapper.getId(it)] = OperationIndicator.ChangedItem
        }

//        delay(UiSettings.Debounce.debounceTime)


        if (changedItems.isNotEmpty() && onItemChanged != null) {
            changedItems.forEach {
                onItemChanged(it)
            }
        }

    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
private fun RenderRow(
    modifier: Modifier = Modifier,
    elevation: Dp = 0.dp,
    renderIndex: @Composable() (RowScope.() -> Unit),
    renderIndicator: @Composable() (BoxScope.() -> Unit)? = null,
    renderColumns: @Composable() (RowScope.() -> Unit),
    renderSelectionControl: @Composable() (BoxScope.() -> Unit)? = null,
    renderDragHandle: @Composable() (RowScope.() -> Unit)? = null,
    onRowClicked: () -> Unit,
    tableWidth: Dp,
    isLastRow: Boolean = false
) {

    var isHover by remember { mutableStateOf(false) }
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        //if indexes are used:
        renderIndex()

        //render cells row:
        Surface(
            elevation = elevation,
            shape = RoundedCornerShape(
                bottomStart = if (isLastRow) UiSettings.DataTable.cornerRadius else 0.dp,
                bottomEnd = if (isLastRow) UiSettings.DataTable.cornerRadius else 0.dp
            )
        ) {
            Box {
                //indicator:
                renderIndicator?.let { ri ->
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .height(UiSettings.DataTable.rowHeight)
                            .width(2.dp)
                            .zIndex(10f)
                    ) {
                        ri()
                    }
                }

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
                        .clickable { onRowClicked() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // selection control:
                    Box(
                        modifier = Modifier.width(UiSettings.DataTable.additionalRowWidth),
                        contentAlignment = Alignment.Center
                    ) {
                        renderSelectionControl?.invoke(this)
                    }
                    renderColumns()

                }
                //divider:
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .height(UiSettings.DataTable.dividerHeight)
                        .width(
                            tableWidth + UiSettings.DataTable.additionalRowWidth
//                                + (if (renderDragHandle != null) UiSettings.DataTable.additionalRowWidth else 0.dp)
                        )
                        .background(color = UiSettings.DataTable.dividerColor())
                )
            }
        }



        renderDragHandle?.invoke(this)
    }

}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BoxScope.RenderCell(
    modifier: Modifier = Modifier,
    cell: Cell,
    columnId: ColumnId,
    onCellChanged: (Cell) -> Unit,
    columnAlignment: ColumnAlignment
) {

    TooltipArea(
        tooltip = {
            // TODO: Show more info in tooltip (not just .toString())
            if (cell.toString().isNotEmpty()) {
                Surface(
                    modifier = Modifier.widthIn(max = UiSettings.DataTable.toolTipWidth),
                    elevation = 16.dp
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(text = columnId.title, style = MaterialTheme.typography.h4)
                        Text(text = cell.toString(), style = MaterialTheme.typography.body1)
                    }
                }
            }
        }, content = {
            when (cell) {
                is Cell.EditTextCell -> RenderEditTextCell(
                    modifier = modifier,
                    cell = cell,
                    onCellChanged = onCellChanged,
                    columnAlignment = columnAlignment
                )

                is Cell.EntityCell -> RenderEntityCell(
                    modifier, cell,
                    onCellChanged = onCellChanged
                )

                is Cell.DateTimeCell -> RenderDateTimeCell(
                    modifier = modifier,
                    cell = cell,
                    onCellChanged = onCellChanged
                )

                is Cell.BooleanCell -> RenderBooleanCell(
                    modifier = modifier,
                    cell = cell,
                    onCellChanged = onCellChanged
                )

                is Cell.ListCell -> TODO()
            }
        })
}

@OptIn(ExperimentalUnitApi::class)
@Composable
private fun getCellTextStyle() =
    LocalTextStyle.current.copy(fontSize = TextUnit(UiSettings.DataTable.cellTextSize, TextUnitType.Sp))


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BoxScope.RenderEditTextCell(
    modifier: Modifier = Modifier,
    cell: Cell.EditTextCell,
    onCellChanged: (Cell) -> Unit,
    columnAlignment: ColumnAlignment
) {

    var value by remember(cell) { mutableStateOf(cell.value) }
    var focusState by remember { mutableStateOf<FocusState?>(null) }

    DataTableEditTextField(
        modifier = modifier.onFocusChanged {
            focusState = it
        },
        value = value,
        singleLine = false,
        onValueChange = { value = it },
        textStyle = getCellTextStyle(),
        trailingIcon = if (value.isNotEmpty()) {
            {
                Icon(
                    Icons.Rounded.Clear,
                    contentDescription = "clear text",
                    modifier = Modifier
                        .size(18.dp)
                        .clickable {
                            value = ""
                        }
                )
            }
        } else null
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
    onCellChanged: (Cell) -> Unit
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
    onCellChanged: (Cell) -> Unit
) {

    DataTableEditTextField(
        modifier = modifier,
        value = cell.value?.let { DateTimeConverter.dateTimeToString(it) } ?: "",
        textStyle = getCellTextStyle(),
        trailingIcon = if (cell.value != null) {
            {
                Icon(
                    Icons.Rounded.Clear,
                    contentDescription = "clear datetime",
                    modifier = Modifier
                        .size(18.dp)
                        .clickable {
                            onCellChanged(cell.copy(value = null))
                        }
                )
            }
        } else null
    )

}


@Composable
private fun BoxScope.RenderEntityCell(
    modifier: Modifier = Modifier,
    cell: Cell.EntityCell,
    onCellChanged: (Cell) -> Unit
) {

    val leadingIcon: (@Composable BoxScope.() -> Unit)? = when (cell.entityClass) {
        domain.Unit::class -> (cell.entity as? domain.Unit)?.let { unit ->
            {
                var showFactorsList by remember { mutableStateOf(false) }
                Box {
                    Text(
                        text = ((cell.tag as? Factor) ?: Factor.NoFactor).prefix,
                        modifier = Modifier.clickable {
                            //show factors list to choose from:
                            showFactorsList = true
                        }.padding(horizontal = 2.dp),
                        style = getCellTextStyle()
                    )

                    DropdownMenu(
                        expanded = showFactorsList,
                        onDismissRequest = { showFactorsList = false }
                    ) {
                        factors
                            .forEach { f ->
                                DropdownMenuItem(
                                    modifier = Modifier
                                        .background(
                                            if (f == cell.tag as? Factor) MaterialTheme
                                                .colors
                                                .secondary
                                                .copy(alpha = 0.5f) else MaterialTheme.colors.surface
                                        ),
                                    onClick = {
                                        onCellChanged(cell.copy(tag = f))
                                        showFactorsList = false
                                    }) {
                                    Text(
                                        text = "${f.prefix}: ${f.name}"
                                    )
                                }
                            }
                    }
                }
            }
        }

        else -> null
    }

//
//    when (cell.entityClass) {
//        domain.Unit::class -> {
//            RenderUnitCell(
//                modifier = modifier,
//                cell = cell,
//                unit = cell.entity as? domain.Unit,
//                factor = cell.tag as? Factor,
//                onCellChanged = onCellChanged
//            )
//        }
//
//        else -> {
    DataTableEditTextField(
        modifier = modifier,
        value = cell.entity?.toString() ?: "",
        leadingIcon = leadingIcon,
        trailingIcon = if (cell.entity != null) {
            {
                Icon(
                    Icons.Rounded.Clear,
                    contentDescription = "clear entity",
                    modifier = Modifier
                        .size(18.dp)
                        .clickable {
                            onCellChanged(cell.copy(entity = null))
                        }
                )
            }
        } else null,
        textStyle = getCellTextStyle()
    )
//        }
//    }

}


data class ColumnId(
    val key: String,    //might correspond to exposed column name for working filtering, sorting and grouping
    val title: String = "",
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
    fun updatePosition(item: T, position: Int): T = item
    fun getId(item: T): String
}

/**
 * Calculate table width based on columns width settings.
 */
fun IDataTableMapper<*>.getTableWidth(): Dp {
    return columns.fold(0.dp) { acc, columnId ->
        acc + when (columnId.width) {
            is ColumnWidth.Custom -> columnId.width.width
            ColumnWidth.Normal -> UiSettings.DataTable.columnDefaultWidthNormal
            ColumnWidth.Small -> UiSettings.DataTable.columnDefaultWidthSmall
            ColumnWidth.Wide -> UiSettings.DataTable.columnDefaultWidthWide
        }
    }
}

sealed class Cell {

    data class EditTextCell(val value: String) : Cell() {
        override fun toString() = value
    }

    data class EntityCell(
        val entity: IEntity?,
        val entityClass: KClass<out IEntity>,
        val tag: Any? = null
    ) : Cell() {
        override fun toString(): String {
            val builder = StringBuilder()
            entity?.let {
                builder.appendLine(it.toString())
            }
            tag?.let {
                builder.appendLine()
                builder.appendLine(it.toString())    //used for factor in units
            }
            return builder.toString()
        }
    }

    data class DateTimeCell(val value: LocalDateTime?) : Cell() {
        override fun toString() = value?.let { DateTimeConverter.dateTimeToString(it) } ?: ""
    }

    data class BooleanCell(val value: Boolean) : Cell() {
        override fun toString() = value.toString()
    }

    data class ListCell(val values: List<String>) : Cell()


}

sealed class SelectionMode {
    object Single : SelectionMode()
    object Multiple : SelectionMode()
}


sealed class OperationIndicator {
    object UpdatedSuccessfully : OperationIndicator()
    object UpdatedWithError : OperationIndicator()

    object ChangedItem : OperationIndicator()
}