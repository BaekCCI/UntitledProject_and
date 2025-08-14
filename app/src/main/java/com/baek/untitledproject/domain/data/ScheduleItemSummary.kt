package com.baek.untitledproject.domain.data

data class ScheduleItemSummary(
    val id: String,                          // 스케줄 ID
    val date: String,                        // 날짜 (MM/dd 형식)
    val time: String,                        // 시간 (HH:mm 형식)
    val organization: String                 // 단체명/동아리명
)