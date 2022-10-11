package ui.dialogs

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.rememberDialogState
import ui.theme.AppTheme
import ui.theme.DialogSettings
import ui.theme.DialogSettings.DatePickerSettings.defaultDateCellSelectionSize
import ui.theme.DialogSettings.DatePickerSettings.defaultDateCellSize
import ui.theme.DialogSettings.DatePickerSettings.defaultIconSize
import ui.theme.DialogSettings.DatePickerSettings.defaultPickerTitleHeight
import ui.theme.DialogSettings.DatePickerSettings.defaultPickerWidth
import ui.theme.DialogSettings.DatePickerSettings.defaultYearCellHeight
import ui.theme.DialogSettings.DatePickerSettings.defaultYearCellSelectionHeight
import ui.theme.DialogSettings.DatePickerSettings.defaultYearCellSelectionWidth
import ui.theme.DialogSettings.DatePickerSettings.defaultYearCellWidth
import utils.DateTimeConverter
import utils.getDates
import utils.log
import utils.yearMonth
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.*


/**
 * Material date picker dialog as described here:
 * https://material.io/components/date-pickers#specs
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DatePickerDialog(
    initialSelection: LocalDate? = null,
    onDismiss: () -> Unit,
    onDateSelected: (LocalDate) -> Unit
) {

    val dialogState = rememberDialogState(
        size = DpSize(
            width = defaultPickerWidth,
            height = DialogSettings.DatePickerSettings.defaultPickerHeight
        )
    )
    Dialog(
        state = dialogState,
        onCloseRequest = onDismiss,
        undecorated = true,
        resizable = false,
        transparent = true,
        content = {
            DatePickerDialogContent(
                initialSelection = initialSelection,
                onCancelClick = onDismiss,
                onOkClick = {
                    onDateSelected(it)
                    onDismiss()
                }
            )
        }
    )

}


@Composable
private fun DatePickerDialogContent(
    initialSelection: LocalDate? = null,
    onCancelClick: (() -> Unit)? = null,
    onOkClick: ((LocalDate) -> Unit)? = null
) {

    var yearMonth by remember(initialSelection) { mutableStateOf(initialSelection?.yearMonth ?: YearMonth.now()) }
    var selectedDate by remember(initialSelection) { mutableStateOf<LocalDate?>(initialSelection) }


    Surface(shape = MaterialTheme.shapes.medium) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            //title
            Box(
                modifier = Modifier.height(defaultPickerTitleHeight)
                    .width(defaultPickerWidth)
                    .background(color = MaterialTheme.colors.primarySurface)
            ) {
                TitleContent(selectedDate)
            }

            //main content
            Box(modifier = Modifier.weight(1f).width(defaultPickerWidth)) {

                MainContent(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = DialogSettings.DatePickerSettings.defaultHorizontalPadding),
                    yearMonth = yearMonth,
                    onYearMonthChange = {
                        yearMonth = it
                    },
                    selectedDate = selectedDate,
                    onDayClick = {
                        selectedDate = it
                    }
                )
            }


            //buttons
            Row(modifier = Modifier.fillMaxWidth().padding(4.dp), horizontalArrangement = Arrangement.End) {
                onCancelClick?.let {
                    TextButton(onClick = it, content = {
                        Text(text = "отмена".uppercase())
                    })
                }
                onOkClick?.let { onClick ->
                    TextButton(onClick = {
                        selectedDate?.let { onClick(it) }
                    }, content = {
                        Text(text = "ок".uppercase())
                    })
                }
            }
        }
    }

}


/**
 * [withMobileInputPicker] - see specs here:
 * https://material.io/components/date-pickers#specs
 */
@Composable
private fun TitleContent(selectedDate: LocalDate? = null, withMobileInputPicker: Boolean = false) {

    Column(modifier = Modifier.padding(horizontal = 24.dp).fillMaxWidth()) {

        Box(modifier = Modifier.height(32.dp)) {
            Text(
                modifier = Modifier.align(Alignment.BottomStart),
                text = "выберите дату",
                style = MaterialTheme.typography.overline,
                color = MaterialTheme.colors.contentColorFor(MaterialTheme.colors.primarySurface)
            )
        }


        Row(modifier = Modifier.height(72.dp), verticalAlignment = Alignment.Bottom) {
            Text(
                modifier = Modifier.weight(1f),
                text = selectedDate?.format(DateTimeConverter.shortFullDate) ?: "",
                style = MaterialTheme.typography.h5,
                color = MaterialTheme.colors.contentColorFor(MaterialTheme.colors.primarySurface)
            )
            if (withMobileInputPicker)
                IconButton(modifier = Modifier.size(defaultIconSize), onClick = {}, content = {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = "изменить дату",
                        tint = MaterialTheme.colors.contentColorFor(MaterialTheme.colors.primarySurface)
                    )
                })
        }


    }
}

/**
 * Main content of date picker:
 * yearmonth dropdown and previous/next buttons at the top
 * month view at the bottom
 */
@Composable
private fun MainContent(
    modifier: Modifier = Modifier,
    yearMonth: YearMonth,
    selectedDate: LocalDate?,
    onYearMonthChange: (YearMonth) -> Unit,
    onDayClick: ((LocalDate) -> Unit)?
) {

    var pickerState by remember { mutableStateOf<DatePickerState>(DatePickerState.DateSelectorState) }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.padding(vertical = 16.dp).height(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = DateTimeConverter.MMMMyyyy.format(yearMonth),
                onValueChange = {
                    try {
                        YearMonth.parse(it, DateTimeConverter.MMMMyyyy)
                    } catch (e: Exception) {
                        log(e.localizedMessage)
                        null
                    }?.let { ym ->
                        onYearMonthChange(ym)
                    }
                }
            )
            IconButton(onClick = {
                pickerState = when (pickerState) {
                    DatePickerState.DateSelectorState -> DatePickerState.YearSelectorState
                    DatePickerState.YearSelectorState -> DatePickerState.DateSelectorState
                }
            }) {
                Icon(
                    modifier = Modifier.size(defaultIconSize).rotate(
                        when (pickerState) {
                            DatePickerState.DateSelectorState -> 0f
                            DatePickerState.YearSelectorState -> 180f
                        }
                    ),
                    imageVector = Icons.Rounded.ArrowDropDown,
                    contentDescription = "pick year month"
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            if (pickerState == DatePickerState.DateSelectorState) {
                IconButton(
                    modifier = Modifier.size(defaultIconSize),
                    onClick = {
                        onYearMonthChange(yearMonth.minusMonths(1L))
                    }) {
                    Icon(imageVector = Icons.Rounded.KeyboardArrowLeft, contentDescription = "previous month")
                }
                Spacer(modifier = Modifier.size(defaultIconSize))
                IconButton(
                    modifier = Modifier.size(defaultIconSize),
                    onClick = {
                        onYearMonthChange(yearMonth.plusMonths(1L))
                    }) {
                    Icon(imageVector = Icons.Rounded.KeyboardArrowRight, contentDescription = "next month")
                }
            }
        }

        when (pickerState) {
            DatePickerState.DateSelectorState -> DateSelectorContent(yearMonth, selectedDate, onDayClick)
            DatePickerState.YearSelectorState -> YearSelectorContent(yearMonth.year, onYearChanged = {
                onYearMonthChange(yearMonth.withYear(it))
                pickerState = DatePickerState.DateSelectorState
            })
        }


    }
}

@Composable
private fun YearSelectorContent(currentYear: Int, onYearChanged: (Int) -> Unit) {

    val startYear = remember { 1970 }
    val endYear = remember { 2050 }
    val yearsInTheRow = remember { 3 }

    val allYearsList = remember { (startYear..endYear).toList().windowed(size = yearsInTheRow, step = yearsInTheRow) }

    val columnState = rememberLazyListState(initialFirstVisibleItemIndex = (currentYear - startYear) / yearsInTheRow)

    LazyColumn(
        state = columnState,
        modifier = Modifier.fillMaxWidth()
    ) {

        items(allYearsList) { yearsRow ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                yearsRow.forEach { y ->
                    YearCell(year = y, isSelected = y == currentYear, onYearClick = onYearChanged)
                }
            }
        }

    }

}

@Composable
private fun YearCell(year: Int, isSelected: Boolean, onYearClick: (Int) -> Unit) {
    Box(
        modifier = Modifier
            .size(width = defaultYearCellWidth, height = defaultYearCellHeight)
            .clickable { onYearClick(year) }
    ) {

        val bgColor = if (isSelected) MaterialTheme.colors.primary else MaterialTheme.colors.surface

        if (isSelected) {
            Surface(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(width = defaultYearCellSelectionWidth, height = defaultYearCellSelectionHeight),
                shape = RoundedCornerShape(defaultYearCellSelectionHeight / 2),
                color = bgColor
            ) {}
        }

        Text(
            modifier = Modifier.align(Alignment.Center),
            text = year.toString(),
            fontWeight = if (year == LocalDate.now().year) {
                FontWeight.Bold
            } else null,
            color = MaterialTheme.colors.contentColorFor(bgColor)
        )
    }
}

@Composable
private fun DateSelectorContent(ym: YearMonth, selectedDate: LocalDate?, onDayClick: ((LocalDate) -> Unit)?) {
    val datesLines: List<List<LocalDate?>> =
        remember(ym) { ym.getDates(withPreviousNextDays = false).windowed(size = 7, step = 7) }
    Column(
        modifier =
        Modifier
            .fillMaxWidth()
//        .border(width = Dp.Hairline, color = Color.DarkGray)
    ) {
        HeaderRow()
        datesLines.forEach { weekLine ->
            WeekRow(dates = weekLine, selectedDate = selectedDate, onDayClick = onDayClick)
        }
    }
}


@Composable
fun HeaderRow() {
    val firstDayOfWeek: DayOfWeek = remember { WeekFields.of(Locale.getDefault()).firstDayOfWeek }
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        for (i in 0 until 7) {
            Box(modifier = Modifier.size(defaultDateCellSize)) {
                Text(
                    modifier = Modifier
                        .align(Alignment.Center),
                    text = DateTimeConverter.dayOfWeekShortest.format(firstDayOfWeek.plus(i.toLong()))
                        .uppercase(),
                    style = MaterialTheme.typography.overline
                )
            }
        }
    }
}

@Composable
fun WeekRow(
    dates: List<LocalDate?>,
    selectedDate: LocalDate? = null,
    onDayClick: ((LocalDate) -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        dates.forEach { date ->
            DayCell(date = date, isSelected = date == selectedDate, onDayClick = onDayClick)
        }
    }
}

@Composable
fun DayCell(
    date: LocalDate?,
    isSelected: Boolean = false,
    onDayClick: ((LocalDate) -> Unit)? = null
) {

    val modifier = Modifier
        .size(defaultDateCellSize)

    Box(
        modifier = date?.let { d ->
            modifier.clickable { onDayClick?.invoke(d) }
        } ?: modifier
    ) {

        val bgColor = if (isSelected) MaterialTheme.colors.primary else MaterialTheme.colors.surface

        if (isSelected && date != null) {
            Surface(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(defaultDateCellSelectionSize),
                shape = CircleShape,
                color = bgColor
            ) {}
        }

        Text(
            modifier = Modifier.align(Alignment.Center),
            text = date?.dayOfMonth?.toString() ?: "",
            fontWeight = if (date == LocalDate.now()) {
                FontWeight.Bold
            } else null,
            color = MaterialTheme.colors.contentColorFor(bgColor)
        )
    }
}

private sealed class DatePickerState {
    object DateSelectorState : DatePickerState()
    object YearSelectorState : DatePickerState()
}

@Preview
@Composable
fun DatePickerTest_light() {
    AppTheme(darkTheme = false) {
        DatePickerDialogContent()
    }
}

@Preview
@Composable
fun DatePickerTest_dark() {
    AppTheme(darkTheme = true) {
        DatePickerDialogContent()
    }
}