package com.baek.untitledproject.domain.data

/**
 * 면접 일정 그룹 요약 정보 (목록용)
 * MyRecruitsFragment, ScheduleGroupAdapter에서 사용
 */
data class ScheduleGroupSummary(
    val id: String,                          // 그룹 ID (예: "group_today", "group_tomorrow")
    val title: String,                       // 그룹 제목 (예: "오늘", "내일", "이번 주")
    val count: Int,                          // 해당 그룹의 일정 개수
    val isExpanded: Boolean,                 // 펼침/접힘 상태
    val scheduleItems: List<ScheduleItemSummary> // 해당 그룹의 일정 아이템들
)