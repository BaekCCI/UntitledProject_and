package com.baek.untitledproject.domain.data

import java.time.LocalDate

data class MyRecruitSummary(
    val id: String,
    val title: String,
    val category: String,
    val recruitStatus: String,
    val thumbnailUrl: String? = null,

    val hasInterview: Boolean,
    val applicantCount: Int = 0,

    val recruitmentStart: LocalDate? = null,
    val recruitmentEnd: LocalDate? = null
)
