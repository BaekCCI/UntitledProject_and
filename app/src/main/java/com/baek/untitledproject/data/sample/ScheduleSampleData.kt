package com.baek.untitledproject.data.sample

import com.baek.untitledproject.domain.data.ScheduleGroupSummary
import com.baek.untitledproject.domain.data.ScheduleItemSummary

object ScheduleSampleData {

    // 모집 단위 일정들
    private val recruitScheduleItems = listOf(
        ScheduleItemSummary(
            id = "r1",
            date = "7.24",
            dayOfWeek = "목",
            time = "15:00",
            organization = "댄스 동아리",
            content = "OOO",
            type = "모집"
        ),
        ScheduleItemSummary(
            id = "r2",
            date = "7.24",
            dayOfWeek = "목",
            time = "15:30",
            organization = "댄스 동아리",
            content = "OOO",
            type = "모집"
        ),
        ScheduleItemSummary(
            id = "r3",
            date = "7.24",
            dayOfWeek = "목",
            time = "16:00",
            organization = "댄스 동아리",
            content = "OOO",
            type = "모집"
        )
    )

    // 지원 단위 일정들
    private val applicationScheduleItems = listOf(
        ScheduleItemSummary(
            id = "a1",
            date = "7.24",
            dayOfWeek = "목",
            time = "18:00",
            organization = "댄스 동아리",
            content = "OOO",
            type = "지원"
        ),
        ScheduleItemSummary(
            id = "a2",
            date = "7.25",
            dayOfWeek = "금",
            time = "15:00",
            organization = "영상 동아리",
            content = "OOO",
            type = "지원"
        )
    )

    // 일정 그룹 목록
    val scheduleGroupList = listOf(
        ScheduleGroupSummary(
            id = "group1",
            title = "모집 단위",
            type = "모집",
            count = recruitScheduleItems.size,
            scheduleItems = recruitScheduleItems,
            isExpanded = false
        ),
        ScheduleGroupSummary(
            id = "group2",
            title = "지원 단위",
            type = "지원",
            count = applicationScheduleItems.size,
            scheduleItems = applicationScheduleItems,
            isExpanded = false
        )
    )
}