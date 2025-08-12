package com.baek.untitledproject.domain.utils

import com.baek.untitledproject.domain.data.Notification
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

enum class DateUiStyle(val pattern: (Char) -> String) {
    YMD_WITH_WEEKDAY({ sep -> "yy${sep}MM${sep}dd${sep}E" }),//  yy/MM/dd/E
    MD_WITH_WEEKDAY({ sep -> "MM${sep}dd${sep}E" }),// MM/dd/E
    MD_KR({ _ -> "MM'월' dd'일'" })
}

fun LocalDate.toUiString(
    style: DateUiStyle,
    separator: Char = '/',
    locale: Locale = Locale.KOREA
): String = DateTimeFormatter.ofPattern(style.pattern(separator), locale).format(this)

fun String.toLocalDate():LocalDate{
    val formatter = DateTimeFormatter.ofPattern("yy/MM/dd/E", Locale.KOREAN)
    return LocalDate.parse(this, formatter)
}

fun Notification.timeText(now: Long): String {
    val diff = (now - createdAt).coerceAtLeast(0)
    val min = diff / 60_000
    val hr = min / 60
    val day = hr / 24
    return when {
        min < 1   -> "방금 전"
        min < 60  -> "${min}분 전"
        hr  < 24  -> "${hr}시간 전"
        day < 7   -> "${day}일 전"
        else -> java.text.SimpleDateFormat("yyyy/MM/dd", Locale.KOREA)
            .format(java.util.Date(createdAt))
    }
}
