package ui.screens.base_entity_screen

import LocalSamplesType
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.rememberDialogState
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import domain.FilterSpec
import domain.IEntity
import domain.Specification
import domain.flatten
import ui.UiSettings
import ui.components.DropdownMenuItemWithIcon
import ui.components.Pagination
import ui.components.tables.*
import ui.dialogs.BaseDialog
import ui.dialogs.DatePickerDialog
import ui.dialogs.TimePickerDialog
import ui.dialogs.add_multiple_samples_dialog.AddMultipleSamplesUi
import ui.dialogs.error_dialog.ErrorDialogUi
import ui.dialogs.file_picker_dialog.FilePickerUi
import ui.dialogs.import_from_file.ImportFromFileUi
import ui.dialogs.input_text_dialog.InputTextDialogUi
import ui.dialogs.list_picker_dialog.ListPickerDialogUi
import ui.dialogs.prompt_dialog.PromptDialogUi
import ui.root_ui.IDialogHandler
import ui.screens.base_entity_screen.filter_dialog.FilterEntityFieldUi
import utils.log
import utils.toFormattedList
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
fun <T : IEntity> IDialogHandler.BaseEntityUi(
    modifier: Modifier = Modifier,
    component: IEntityComponent<T>,
    selectionMode: SelectionMode? = remember { SelectionMode.Multiple },
    initialSelection: List<String> = remember { listOf() },
    onSelectionChanged: ((List<T>) -> Unit)? = null,
    onDialogShowing: ((isDialogShowing: Boolean) -> Unit)? = null
) {
    val state by remember(component) { component.state }.subscribeAsState()

    val sampleType = LocalSamplesType.current

    val currentDialog by remember(component) { component.dialogStack }.subscribeAsState()

    LaunchedEffect(currentDialog) {
        setShowingDialog(
            when (currentDialog.active.instance) {
                IEntityComponent.Dialog.None -> false
                else -> true
            }
        )
//        onDialogShowing?.let {
//
//            it(
//                when (currentDialog.active.instance) {
//                    IEntityComponent.Dialog.None -> false
//                    else -> true
//                }
//            )
//        }
    }

    LaunchedEffect(sampleType) {
        sampleType?.let {
            component.setSampleType(it)
        }
    }

    //fixme: now showing flatten groups since there is no grouping implemented.
    ShowDataTableForGroup(
        modifier = modifier,
        entities = state.entities.flatten(),
        component = component,
        selectionMode = selectionMode,
        initialSelection = initialSelection,
        onSelectionChanged = onSelectionChanged
    )


    Children(stack = component.dialogStack) {
        when (val dialog = it.instance) {
            is IEntityComponent.Dialog.EntityPicker<*> -> {
                val childTableWidth = remember { dialog.component.dataMapper.value.getTableWidth() + 128.dp }
                val dialogState = rememberDialogState(
                    width = childTableWidth,
                    height = UiSettings.Dialogs.defaultWideDialogHeight
                )

                var selection by remember { mutableStateOf(dialog.initialSelection) }

                BaseDialog(
                    dialogState = dialogState,
                    onDismiss = { component.dismissDialog() },
                    title = {
                        Box(modifier = Modifier.height(64.dp).padding(horizontal = 24.dp, vertical = 8.dp)) {
                            Text(
                                modifier = Modifier.align(Alignment.BottomStart),
                                text = "Выбрать: ${dialog.columnName.lowercase()}"
                            )
                        }
                    },
                    content = {
                        BaseEntityUi(
                            component = remember(dialog) { dialog.component },
                            selectionMode = remember { SelectionMode.Single },
                            initialSelection = remember(dialog) { listOfNotNull(dialog.initialSelection?.id) },
                            onSelectionChanged = { s ->
                                selection = s.firstOrNull()
                            }
                        )
                    },
                    buttons = {

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
                                            dialog.component.removeEntites(listOf(it))
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
                                dialog.onSelectionChanged(selection)
                                component.dismissDialog()
                            }) {
                            Text("Выбрать")
                        }

                    }
                )
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

            is IEntityComponent.Dialog.AddMultipleSamplesDialog -> {
                AddMultipleSamplesUi(component = dialog.component, onAdded = dialog.onAdd) { component.dismissDialog() }
            }

            is IEntityComponent.Dialog.ImportEntitiesDialog -> {
                ImportFromFileUi(component = dialog.component) {
                    component.dismissDialog()
                }
            }

            is IEntityComponent.Dialog.InputTextDialog -> {
                InputTextDialogUi(component = dialog.component) {
                    component.dismissDialog()
                }
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
    initialSelection: List<String> = listOf(),
    onSelectionChanged: ((List<T>) -> Unit)? = null
) {
    var dateTimePickerParams by remember { mutableStateOf<DateTimePickerDialogParams?>(null) }

    val selectedEntities = remember(initialSelection) { initialSelection.toSet().toMutableStateList() }

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

        var isHovered by remember { mutableStateOf(false) }
        val showControl = remember(selectionMode, selectedEntities.toList()) {
            selectionMode == SelectionMode.Multiple && selectedEntities.isNotEmpty()
        }
        val alpha by animateFloatAsState(if (isHovered || showControl) 1f else 0.4f)


        DataTable(
            modifier = modifier
//                .horizontalScroll(state = rememberScrollState())
            ,
            items = entities,
            selection = selectedEntities,
            onSelectionChanged = remember(entities) {
                { items, areSelected ->
                    log("onSelectionChanged: $items areSelected: $areSelected")
                    val ids = items.map { it.id }
                    if (areSelected) {
                        selectedEntities.addAll(ids)
                    } else {
                        selectedEntities.removeIf { it in ids }
                    }
                    log("entities: $entities")
                    onSelectionChanged?.invoke(entities.filter { it.id in selectedEntities })
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
            footer = {
                Surface(
                    modifier = Modifier
                        .alpha(alpha)
                        .fillMaxWidth()
                        .onPointerEvent(PointerEventType.Enter) { isHovered = true }
                        .onPointerEvent(PointerEventType.Exit) { isHovered = false },
                    elevation = 8.dp
                ) {
                    if (showControl) {
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
                                        val es = entities.filter { it.id in selectedEntities }
                                        component.showPrompt(
                                            title = "Удалить объекты (${es.size})?",
                                            message = "${es.toFormattedList()}",
                                            onYes = {
                                                //do actual delete
                                                component.removeEntites(es)
                                                es.forEach {
                                                    selectedEntities.remove(it.id)
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
//                show pagination control:
                        pagingSpec.totalItems?.let { ti ->
                            if (ti > pagingSpec.itemsPerPage) {
                                Pagination(
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