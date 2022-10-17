package ui.screens.base_entity_screen

import LocalSamplesType
import androidx.compose.animation.core.animateFloatAsState
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
import ui.dialogs.file_picker_dialog.FilePickerUi
import ui.dialogs.list_picker_dialog.ListPickerDialogUi
import ui.dialogs.prompt_dialog.PromptDialogUi
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
    selectionMode: SelectionMode? = SelectionMode.Multiple,
    initialSelection: List<String> = listOf()
) {
    val state by remember(component) { component.state }.subscribeAsState()

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
                selectionMode = selectionMode,
                initialSelection = initialSelection
            )
        }
    }

    Children(stack = component.dialogStack) {
        when (val dialog = it.instance) {
            is IEntityComponent.Dialog.EntityPicker<*> -> {
                val childTableWidth = remember { dialog.component.dataMapper.value.getTableWidth() + 128.dp }
                val dialogState = rememberDialogState(
                    width = childTableWidth,
                    height = UiSettings.Dialogs.defaultWideDialogHeight
                )

                var selection by remember { mutableStateOf(dialog.initialSelection) }

                Dialog(
                    state = dialogState,
                    title = "Выбрать: ${dialog.columnName.lowercase()}",
                    onCloseRequest = {
                        component.dismissDialog()
                    }) {
                    Surface {
                        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(modifier = Modifier.weight(1f)) {
                                BaseEntityUi(
                                    component = dialog.component,
                                    selectionMode = SelectionMode.Single,
                                    initialSelection = listOfNotNull(dialog.initialSelection?.id)
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
                                        dialog.component.insertNewEntity(it)
                                    }
                                }) {
                                    Text("Добавить")
                                }

                                TextButton(
                                    enabled = selection != null,
                                    onClick = {
                                        dialog.component.showPrompt(
                                            title = "Удалить объект?",
                                            message = "$selection",
                                            onYes = {
                                                //do actual delete
                                                selection?.let {
                                                    dialog.component.removeEntity(it)
                                                    selection = null
                                                }

                                            }
                                        )
                                    },
                                    colors = ButtonDefaults.textButtonColors(
                                        contentColor = MaterialTheme.colors.error,
                                        disabledContentColor = MaterialTheme.colors.error.copy(alpha = 0.25f)
                                    )
                                ) {
                                    Text("Удалить")
                                }

                                Button(
                                    enabled = selection != null,
                                    onClick = {
//                                component.updateEntity()
                                        dialog.onSelectionChanged(selection)
                                        component.dismissDialog()
                                    }) {
                                    Text("Выбрать")
                                }
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
                FilterEntityFieldUi(component = dialog.component, onDismissDialog = {
                    component.dismissDialog()
                })
            }

            is IEntityComponent.Dialog.ErrorDialog -> {
                ErrorDialogUi(component = dialog.component, onDismissDialog = {
                    component.dismissDialog()
                })
            }

            is IEntityComponent.Dialog.PromptDialog -> {
                PromptDialogUi(
                    component = dialog.component,
                    onDismiss = { component.dismissDialog() },
                    onYes = dialog.onYes,
                    onCancel = dialog.onCancel
                )
            }

            is IEntityComponent.Dialog.FilePickerDialog -> {
                FilePickerUi(component = dialog.component) { component.dismissDialog() }
            }

            is IEntityComponent.Dialog.ListPickerDialog -> {
                ListPickerDialogUi(component = dialog.component) { component.dismissDialog() }
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
    selectionMode: SelectionMode?,
    initialSelection: List<String> = listOf()
) {
    var dateTimePickerParams by remember { mutableStateOf<DateTimePickerDialogParams?>(null) }

    val selectedEntities = remember(initialSelection) { initialSelection.toSet().toMutableStateList() }

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
            selection = selectedEntities,
            onSelectionChanged = remember {
                { items, areSelected ->
                    log("onSelectionChanged: $items areSelected: $areSelected")
                    val ids = items.map { it.id }
                    if (areSelected) {
                        selectedEntities.addAll(ids)
                    } else {
                        selectedEntities.removeIf { it in ids }
                    }
                }
            },
            selectionMode = selectionMode,
            mapper = mapper,
            onItemChanged = { component.updateEntity(it) },
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
            footer = null,
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
            if (selectionMode == SelectionMode.Multiple && selectedEntities.isNotEmpty()) {

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
                            component.duplicateEntities(entities.filter { it.id in selectedEntities })
                        }) {
                            Text(text = "Дублировать")
                        }


                        Button(onClick = {
                            component.shareEntities(entities.filter { it.id in selectedEntities })

                        }) {
                            Text(text = "Отправить")
                        }

                        TextButton(
                            onClick = {
                                component.showPrompt(
                                    title = "Удалить объекты?",
                                    message = "${selectedEntities.toList()}?",
                                    onYes = {
                                        //do actual delete
                                        selectedEntities.toList().forEach {
                                            component.removeEntity(it)
                                            selectedEntities.remove(it)
                                        }
                                    }
                                )
                            },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colors.error,
                                disabledContentColor = MaterialTheme.colors.error.copy(alpha = 0.25f)
                            )
                        ) {
                            Text(text = "Удалить", color = MaterialTheme.colors.error)
                            Icon(
                                Icons.Rounded.Delete,
                                contentDescription = "Удалить записи"
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