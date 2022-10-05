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
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.rememberDialogState
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import domain.EntitiesList
import domain.IEntity
import domain.Specification
import ui.UiSettings
import ui.components.Pagination
import ui.components.tables.*
import ui.dialogs.DatePickerDialog
import ui.dialogs.TimePickerDialog
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

                val sampleType = LocalSamplesType.current

                Dialog(
                    state = dialogState,
                    title = "Выбрать: ${child.columnName.lowercase()}",
                    onCloseRequest = {
                        component.dismissDialog()
                    }) {
                    Column(modifier = Modifier.fillMaxSize()) {
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
        }
    }

}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun <T : IEntity> ShowDataTableForGroup(
    modifier: Modifier = Modifier,
    entities: List<T>,
    component: IEntityComponent<T>,
    onHeaderClicked: ((ColumnId) -> Unit)? = null,
    selectionMode: SelectionMode<T>
) {
    var dateTimePickerParams by remember { mutableStateOf<DateTimePickerDialogParams?>(null) }

    var selectedEntities = remember { mutableStateListOf<T>() }

    var bottomPanelHeight by remember { mutableStateOf(0.dp) }

    val pagingSpec by remember(component) { component.pagingSpec }.subscribeAsState()

    val mapper by remember(component) { component.dataMapper }.subscribeAsState()

    Box(modifier = modifier.fillMaxHeight()) {

        //parameters table:
        DataTable(
            modifier = modifier
                .horizontalScroll(state = rememberScrollState()),
            items = entities,
            onItemChanged = {
                component.updateEntity(it)
            },
            onSortingChanged = { column, isAsc ->
                component.setQuerySpec(Specification.Sorted(column, isAsc))
            },
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
            //wrapping selection mode to make additional actions available (duplicating/deleting)
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
            footer = {
                Spacer(modifier = Modifier.height(bottomPanelHeight))
            },
            firstItemIndex = remember(pagingSpec) { ((pagingSpec.pageNumber - 1) * pagingSpec.itemsPerPage + 1).toInt() },
            isReorderable = remember(component) { component.isReorderable },
            mapper = mapper
        )

        if (selectionMode is SelectionMode.Multiple<T> && selectedEntities.isNotEmpty()) {
            //show control buttons:
            Surface(
                modifier = Modifier.align(Alignment.BottomCenter).onSizeChanged { bottomPanelHeight = it.height.dp }) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),

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
            }
        } else {
            //show pagination control:
            pagingSpec.totalItems?.let { ti ->

                var isHovered by remember { mutableStateOf(false) }

                val alpha by animateFloatAsState(if (isHovered) 1f else 0.1f)

                Surface(modifier = Modifier.align(Alignment.BottomCenter).onSizeChanged {
                    bottomPanelHeight = it.height.dp
                }.onPointerEvent(PointerEventType.Enter) { isHovered = true }
                    .onPointerEvent(PointerEventType.Exit) { isHovered = false }.alpha(alpha)) {
                    Pagination(
                        modifier = modifier,
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