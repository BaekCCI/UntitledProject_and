package com.baek.untitledproject.domain.data

import java.time.LocalDate
import java.time.LocalTime

//인터뷰 일정 설정 시 사용
sealed class InterviewTimeSlot {
    data class DateHeader(val date: LocalDate) : InterviewTimeSlot()

    data class SlotRow(
        val date: LocalDate,
        val index: Int,
        val start: LocalTime,
        val end: LocalTime,
        var isLast : Boolean
    ) : InterviewTimeSlot()
}

data class TimeSlot(
    val start: LocalTime,
    val end: LocalTime
)
