package com.baek.untitledproject.data.sample

import com.baek.untitledproject.domain.data.AppliedRecruitSummary

object AppliedRecruitSampleData {

    val appliedRecruitList = listOf(
        AppliedRecruitSummary(
            id = "applied1",
            category = "영상동아리",
            title = "영상동아리 OO 모집합니다.",
            recruitStatus = "모집중",
            applicationStatus = "서류합격",
            interviewDate = "2024.07.30 15:00",
            applicationDate = "2024.07.20"
        )
    )
}