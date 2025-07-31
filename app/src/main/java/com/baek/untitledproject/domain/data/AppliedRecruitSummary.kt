package com.baek.untitledproject.domain.data

data class AppliedRecruitSummary(
    val id: String,
    val category: String,        // "영상동아리"
    val title: String,           // "영상동아리 OO 모집합니다."
    val recruitStatus: String,   // "모집중", "모집완료"
    val applicationStatus: String, // "지원완료", "서류합격", "최종합격", "불합격"
    val interviewDate: String?,    // 면접 날짜 (있으면)
    val applicationDate: String    // 지원한 날짜
)