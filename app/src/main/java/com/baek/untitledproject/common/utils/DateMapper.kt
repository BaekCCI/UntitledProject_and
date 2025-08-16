package com.baek.untitledproject.common.utils

import com.google.firebase.Timestamp
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

private val KST: ZoneId = ZoneId.of("Asia/Seoul")

fun LocalDate.toTimestamp(): Timestamp {
    val instant = this.atStartOfDay(KST).toInstant()
    return Timestamp(Date.from(instant))
}

fun Timestamp.toLocalDate(): LocalDate {
    return this.toDate().toInstant()
        .atZone(KST)
        .toLocalDate()
}