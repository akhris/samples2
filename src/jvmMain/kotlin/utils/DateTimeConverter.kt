package utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object DateTimeConverter {

    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.LL.yyyy HH:mm:ss")
    val dayOfWeekShort: DateTimeFormatter = DateTimeFormatter.ofPattern("EE")
    val dayOfWeekShortest: DateTimeFormatter = DateTimeFormatter.ofPattern("EEEEE")
    val MMMMyyyy: DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")
    val shortFullDate: DateTimeFormatter = DateTimeFormatter.ofPattern("EEE, LLL dd")
    fun dateTimeToString(dateTime: LocalDateTime): String {
        return dateTime.format(formatter)
    }

    fun stringToDateTime(formattedString: String): LocalDateTime? {
        return try {
            LocalDateTime.parse(formattedString, formatter)
        } catch (e: DateTimeParseException) {
            null
        }
    }

}