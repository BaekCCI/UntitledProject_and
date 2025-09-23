package com.baek.untitledproject.domain.utils

import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import com.baek.untitledproject.domain.data.Notification
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
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

fun String.toLocalDate(): LocalDate {
    val formatter = DateTimeFormatter.ofPattern("yy/MM/dd/E", Locale.KOREAN)
    return LocalDate.parse(this, formatter)
}

fun toDateRange(
    start: LocalDate, end: LocalDate, style: DateUiStyle
): String {
    val formattedStart = start.toUiString(style)
    val formattedEnd = end.toUiString(style)
    return "$formattedStart ~ $formattedEnd"
}
private val KST: ZoneId = ZoneId.of("Asia/Seoul")

fun Long.toLocalDate():LocalDate{
    return Instant.ofEpochMilli(this)
        .atZone(KST)
        .toLocalDate()
}

fun Notification.timeText(now: Long): String {
    val diff = (now - createdAt).coerceAtLeast(0)
    val min = diff / 60_000
    val hr = min / 60
    val day = hr / 24
    return when {
        min < 1 -> "방금 전"
        min < 60 -> "${min}분 전"
        hr < 24 -> "${hr}시간 전"
        day < 7 -> "${day}일 전"
        else -> java.text.SimpleDateFormat("yyyy/MM/dd", Locale.KOREA)
            .format(java.util.Date(createdAt))
    }
}

//검색 시 글자 색 변경
fun CharSequence.highlightQuery(
    query: String,
    color: Int
): CharSequence {
    val q = query.trim()
    if (q.isEmpty()) return this

    // 공백 여러개 허용 → 토큰 사이를 \s+ 로 매칭
    val tokens = q.split(Regex("\\s+")).filter { it.isNotBlank() }
    if (tokens.isEmpty()) return this

    val pattern = tokens.joinToString(separator = "\\s+") { Regex.escape(it) }
    val regex = Regex(pattern, RegexOption.IGNORE_CASE)

    val match = regex.find(this) ?: return this

    return SpannableString(this).apply {
        setSpan(
            ForegroundColorSpan(color),
            match.range.first, match.range.last + 1,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
}
