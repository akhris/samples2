package ui.screens.base_entity_screen

import LocalSamplesType
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.rememberDialogState
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import domain.EntitiesList
import domain.FilterSpec
import domain.IEntity
import domain.Specification
import ui.UiSettings
import ui.components.DropdownMenuItemWithIcon
import ui.components.Pagination
import ui.components.tables.*
import ui.dialogs.DatePickerDialog
import ui.dialogs.TimePickerDialog
import ui.screens.base_entity_screen.filter_dialog.FilterEntityFieldUi
import ui.dialogs.error_dialog.ErrorDialogUi
import utils.log
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjusters

/**
 * Base Entity Screen:
 * 1. contains only DataTable
 * 2. handles cells clicks/selection
 * 3. shows EntityPicker dialog
 */
@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun <T : IEntity> BaseEntityUi(
    modifier: Modifier = Modifier,
    component: IEntityComponent<T>,
    selectionMode: SelectionMode<T> = SelectionMode.Multiple()
) {
    val state by remember(component) { component.state }.subscribeAsState()
    //render samples list:
//    val entities = remember(state) { state.entities }

    val sampleType = LocalSamplesType.current

    LaunchedEffect(sampleType) {
        sampleType?.let {
            component.setSampleType(it)
        }
    }

    when (val entities = state.entities) {
        is EntitiesList.Grouped -> {

        }

        is EntitiesList.NotGrouped -> {
            ShowDataTableForGroup(
                modifier = modifier,
                entities = entities.items,
                component = component,
                selectionMode = selectionMode
            )
        }
    }

    Children(stack = component.dialogStack) {
        when (val child = it.instance) {
            is IEntityComponent.Dialog.EntityPicker<*> -> {
                val childTableWidth = remember { child.component.dataMapper.value.getTableWidth() + 128.dp }
                val dialogState = rememberDialogState(
                    width = childTableWidth,
                    height = UiSettings.Dialogs.defaultWideDialogHeight
                )

                var selection by remember { mutableStateOf<IEntity?>(null) }

                Dialog(
                    state = dialogState,
                    title = "Выбрать: ${child.columnName.lowercase()}",
                    onCloseRequest = {
                        component.dismissDialog()
                    }) {
                    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(modifier = Modifier.weight(1f)) {
                            BaseEntityUi(
                                component = child.component,
                                selectionMode = SelectionMode.Single(child.initialSelection, onItemSelected = {
                                    selection = it
                                })
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
                        ) {
                            TextButton(onClick = { component.dismissDialog() }) {
                                Text("Отмена")
                            }
                            TextButton(onClick = {
                                sampleType?.let {
                                    child.component.insertNewEntity(it)
                                }
                            }) {
                                Text("Добавить")
                            }
                            Button(onClick = {
//                                component.updateEntity()
                                child.onSelectionChanged(selection)
                                component.dismissDialog()
                            }) {
                                Text("Выбрать")
                            }
                        }
                    }
                }
            }

            IEntityComponent.Dialog.None -> {
                //render nothing
            }

            is IEntityComponent.Dialog.FieldFilter<*> -> {
                //show filtering dialog ui here:
                FilterEntityFieldUi(component = child.component, onDismissDialog = {
                    component.dismissDialog()
                })
            }

            is IEntityComponent.Dialog.ErrorDialog -> {
                ErrorDialogUi(component = child.component, onDismissDialog = {
                    component.dismissDialog()
                })
            }
        }
    }

}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun <T : IEntity> ShowDataTableForGroup(
    modifier: Modifier = Modifier,
    entities: List<T>,
    component: IEntityComponent<T>,
    selectionMode: SelectionMode<T>
) {
    var dateTimePickerParams by remember { mutableStateOf<DateTimePickerDialogParams?>(null) }

    val selectedEntities = remember { mutableStateListOf<T>() }

    var bottomPanelHeight by remember { mutableStateOf(0.dp) }

    val pagingSpec by remember(component) { component.pagingSpec }.subscribeAsState()

    val mapper by remember(component) { component.dataMapper }.subscribeAsState()

    val filterSpec by remember(component) { component.filterSpec }.subscribeAsState()

    val filters = remember(filterSpec) {
        filterSpec.filters.associateBy { it.columnName }.toList().toMutableStateMap()
    }

    var sorting by remember { mutableStateOf<Pair<ColumnId, Boolean>?>(null) }

    LaunchedEffect(sorting) {
        //change query spec on sorting changed:
        sorting?.let { s ->
            component.setQuerySpec(Specification.Sorted(s.first, s.second))
        }
    }

    Box(modifier = modifier.fillMaxHeight()) {
        //parameters table:
        DataTable(
            modifier = modifier.horizontalScroll(state = rememberScrollState()),
            items = entities,
            mapper = mapper,
            onItemChanged = { component.updateEntity(it) },
            selectionMode = remember(selectionMode) {
                when (selectionMode) {
                    is SelectionMode.Multiple -> SelectionMode.Multiple(
                        initialSelection = selectionMode.initialSelection,
                        onItemsSelected = {
                            selectionMode.onItemsSelected?.invoke(it)
                            selectedEntities.clear()
                            selectedEntities.addAll(it)
                        }
                    )

                    is SelectionMode.None -> SelectionMode.None()
                    is SelectionMode.Single -> SelectionMode.Single(
                        initialSelection = selectionMode.initialSelection,
                        onItemSelected = {
                            selectionMode.onItemSelected?.invoke(it)
                            selectedEntities.clear()
                            it?.let {
                                selectedEntities.add(it)
                            }
                        }
                    )
                }
            },
            //wrapping selection mode to make additional actions available (duplicating/deleting)
            onCellClicked = { item, cell, column ->
                when (cell) {
                    is Cell.EntityCell -> {
                        component.showEntityPickerDialog(
                            entity = cell.entity,
                            entityClass = cell.entityClass,
                            onSelectionChanged = {
                                val updatedItem = mapper.updateItem(
                                    item = item,
                                    columnId = column,
                                    cell = cell.copy(entity = it),
                                )
                                component.updateEntity(updatedItem)
                            },
                            columnName = column.title
                        )
                    }

                    is Cell.DateTimeCell -> {
                        dateTimePickerParams =
                            DateTimePickerDialogParams(initialDateTime = cell.value,
                                onDateChanged = {
                                    val updatedItem =
                                        mapper.updateItem(
                                            item,
                                            columnId = column,
                                            cell = cell.copy(value = it),
                                        )
                                    component.updateEntity(updatedItem)
                                })
                    }

                    is Cell.EditTextCell -> {
                    }

                    is Cell.BooleanCell -> TODO()
                    is Cell.ListCell -> TODO()
                }
            },
            onItemRowClicked = {
                component.onEntitySelected(it)
            },
            utilitiesPanel = null,
            footer =
            {
                Box(modifier = Modifier.height(bottomPanelHeight).width(20.dp).background(color = Color.Yellow))
            },
            headerMenu = { column ->

                val sortingAsc = remember(sorting, column) {
                    if (sorting?.first == column) {
                        sorting?.second
                    } else null
                }

                DropdownMenuItemWithIcon(onClick = {
                    //trigger sorting
                    sorting = column to !(sortingAsc ?: false)
                }, text = {
                    Text("Сортировка")
                }, icon = {
                    Icon(
                        modifier = Modifier.scale(scaleY = if (sortingAsc == true) -1f else 1f, scaleX = 1f),
                        painter = painterResource("vector/sort_black_24dp.svg"),
                        contentDescription = "sort",
                        tint = when (sortingAsc) {
                            null -> contentColorFor(
                                MaterialTheme.colors.background
                            ).copy(alpha = 0.5f)

                            else -> MaterialTheme.colors.secondary
                        }
                    )
                })
                DropdownMenuItemWithIcon(onClick = {
                    //show menu for filtering selection
                    val columnFiltersSpec =
                        filterSpec.filters.firstOrNull { it.columnName == column.key } ?: FilterSpec.Values(
                            listOf(),
                            columnName = column.key
                        )
                    component.showFilterDialog(columnFiltersSpec)
                }, text = {
                    Text("Фильтр")
                }, icon = {
                    val columnFilters = remember(filters.values.toList(), column) { filters[column.key] }
                    Icon(
                        painterResource("vector/filter_list_black_24dp.svg"),
                        contentDescription = "filter",
                        tint = if (columnFilters != null) MaterialTheme.colors.secondary else contentColorFor(
                            MaterialTheme.colors.background
                        ).copy(alpha = 0.5f)
                    )
                })
            },
            headerStateIcons = { column ->
                //show filter icon:
                if (filters[column.key] != null) {
                    Icon(
                        modifier = Modifier.size(UiSettings.DataTable.headerStateIconsSize),
                        painter = painterResource("vector/filter_list_black_24dp.svg"),
                        contentDescription = "filter is active",
                        tint = MaterialTheme.colors.secondary
                    )
                }
                //show sorting icon:
                val sortingAsc = remember(sorting, column) {
                    if (sorting?.first == column) {
                        sorting?.second
                    } else null
                }
                sortingAsc?.let { isAsc ->
                    Icon(
                        modifier = Modifier.scale(scaleY = if (isAsc) -1f else 1f, scaleX = 1f)
                            .size(UiSettings.DataTable.headerStateIconsSize),
                        painter = painterResource("vector/sort_black_24dp.svg"),
                        contentDescription = "sorting is active",
                        tint = MaterialTheme.colors.secondary
                    )
                }

            },
            firstItemIndex = remember(pagingSpec) { ((pagingSpec.pageNumber - 1) * pagingSpec.itemsPerPage + 1).toInt() },
            isReorderable = remember(component) { component.isReorderable }
        )
        Surface(
            modifier = Modifier.align(Alignment.BottomCenter).onSizeChanged { bottomPanelHeight = it.height.dp },
            shape = MaterialTheme.shapes.medium,
            elevation = 16.dp
        ) {
            if (selectionMode is SelectionMode.Multiple<T> && selectedEntities.isNotEmpty()) {

                //show control buttons:
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    ) {
                        Button(onClick = {
                            component.duplicateEntities(selectedEntities)
                        }) {
                            Text(text = "Дублировать")
                        }


                        Button(onClick = {
                            component.saveRowsToExcel(selectedEntities)
                        }) {
                            Text(text = "Отправить")
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
                    Text(
                        text = "(${selectedEntities.size} элемент${
                            when (selectedEntities.size % 10) {
                                1 -> ""
                                2, 3, 4 -> "a"
                                else -> "ов"
                            }
                        })", style = MaterialTheme.typography.caption
                    )
                }

            } else {
                //show pagination control:
                pagingSpec.totalItems?.let { ti ->
                    if (ti > pagingSpec.itemsPerPage) {
                        var isHovered by remember { mutableStateOf(false) }
                        val alpha by animateFloatAsState(if (isHovered) 1f else 0.1f)
                        Pagination(
                            modifier = modifier.onPointerEvent(PointerEventType.Enter) { isHovered = true }
                                .onPointerEvent(PointerEventType.Exit) { isHovered = false }.alpha(alpha),
                            currentPage = pagingSpec.pageNumber.toInt(),
                            onPageChanged = { component.setPagingSpec(pagingSpec.copy(pageNumber = it.toLong())) },
                            rowsPerPage = pagingSpec.itemsPerPage.toInt(),
                            onRowsPerPageChanged = { component.setPagingSpec(pagingSpec.copy(itemsPerPage = it.toLong())) },
                            maxItemsCount = ti
                        )
                    }
                }
            }
        }
    }


    dateTimePickerParams?.let { params ->

        var dateTime by remember(dateTimePickerParams) { mutableStateOf(params.initialDateTime) }

        var datePickerDialogShow by remember(dateTimePickerParams) { mutableStateOf(true) }
        var timePickerDialogShow by remember(dateTimePickerParams) { mutableStateOf(false) }

        if (datePickerDialogShow) {
            DatePickerDialog(
                initialSelection = dateTime?.toLocalDate(),
                onDismiss = {
                    datePickerDialogShow = false
                    if (!timePickerDialogShow)
                        dateTimePickerParams = null
                },
                onDateSelected = { newDate ->
                    dateTime =
                        (dateTime ?: LocalDateTime.now())
                            .with(TemporalAdjusters.ofDateAdjuster {
                                newDate
                            }
                            )
                    timePickerDialogShow = true
                })

        }
        if (timePickerDialogShow) {
            TimePickerDialog(initialTime = dateTime, onDismiss = { dateTimePickerParams = null }, onTimeSelected = {
                params.onDateChanged(it)
            })
        }

    }
}

data class DateTimePickerDialogParams(val initialDateTime: LocalDateTime?, val onDateChanged: (LocalDateTime?) -> Unit)