package com.baek.untitledproject.domain.data

data class ScheduleItemSummary(
    val id: String,
    val date: String,        // "7.24"
    val dayOfWeek: String,   // "목"
    val time: String,        // "15:00"
    val organization: String, // "댄스 동아리"
    val content: String,     // "OOO"
    val type: String         // "모집" 또는 "지원"
)