package com.baek.untitledproject.domain.data

data class ScheduleGroupSummary(
    val id: String,
    val title: String,           // "모집 단위 3건"
    val type: String,            // "모집" 또는 "지원"
    val count: Int,              // 3
    val scheduleItems: List<ScheduleItemSummary>,
    val isExpanded: Boolean = false  // 펼침/접힘 상태
)