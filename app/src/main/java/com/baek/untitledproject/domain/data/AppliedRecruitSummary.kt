package com.baek.untitledproject.domain.data

import java.time.LocalDate

data class AppliedRecruitSummary(
    val id: String,
    val postId: String,
    val title: String,
    val category: String,
    val recruitStatus: String,
    val applicationStatus: String,
    val thumbnailUrl: String? = null,

    val hasInterview: Boolean = false,
    val interviewSlotId: String? = null,
    val canReserveInterview: Boolean = false,

    val recruitmentStart: LocalDate? = null,
    val recruitmentEnd: LocalDate? = null
)
