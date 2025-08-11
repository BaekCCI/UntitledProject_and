package com.baek.untitledproject.domain.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale



fun LocalDate.toStringWithDayOfWeekAndSplitter(): String {
    val formatter = DateTimeFormatter.ofPattern("yy/MM/dd/E", Locale.KOREAN)
    return this.format(formatter)
}

fun String.toLocalDate():LocalDate{
    val formatter = DateTimeFormatter.ofPattern("yy/MM/dd/E", Locale.KOREAN)
    return LocalDate.parse(this, formatter)
}
