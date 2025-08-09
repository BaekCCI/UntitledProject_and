package com.baek.untitledproject.domain.data

data class ApplicantSummary(
    val id: String,
    val name: String,           // "제이름"
    val gender: String,         // "남", "여"
    val age: Int,              // 24
    val department: String,     // "커뮤니케이션학과"
    val status: String,        // "submitted", "interview_scheduled", "interview_completed", "passed", "failed"
    val statusText: String,    // "지원서 제출 완료", "면접 예약 완료", "면접 완료", "합격", "불합격"
    val applicationDate: String,  // "2024-08-15"
    val phoneNumber: String? = null,
    val email: String? = null,
    val portfolioUrl: String? = null,
    val interviewDate: String? = null,  // 면접 일정이 있는 경우
    val interviewTime: String? = null,  // "14:00-14:30"
    val notes: String? = null           // 관리자 메모
)