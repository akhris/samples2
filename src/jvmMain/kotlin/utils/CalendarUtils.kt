package utils

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.*

fun YearMonth.getDates(
    firstDayOfWeek: DayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek,
    withPreviousNextDays: Boolean = true
): List<LocalDate?> {
    val dates: MutableList<LocalDate?> = mutableListOf()
    var firstDayOfWeekInMonth = atDay(1).with(TemporalAdjusters.previousOrSame(firstDayOfWeek))
    val firstDayOfWeekNextMonth = atEndOfMonth().with(TemporalAdjusters.next(firstDayOfWeek))
    while (firstDayOfWeekInMonth.isBefore(firstDayOfWeekNextMonth)) {
        if (YearMonth.from(firstDayOfWeekInMonth).equals(this) or withPreviousNextDays)
            dates.add(firstDayOfWeekInMonth)
        else dates.add(null)
        firstDayOfWeekInMonth = firstDayOfWeekInMonth.plusDays(1)
    }
    return dates
}

val LocalDate.yearMonth: YearMonth
    get() {
        return YearMonth.of(year, month)
    }