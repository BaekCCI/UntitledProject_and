package com.baek.untitledproject.domain.data

// 임시
data class ApplicantSummary(
    val id: String,
    val name: String,
    val gender: String,
    val age: Int,
    val department: String,
    val status: String,
    val statusText: String,
    val applicationDate: String,
    val phoneNumber: String? = null,
    val email: String? = null,
    val portfolioUrl: String? = null,
    val interviewDate: String? = null,
    val interviewTime: String? = null,
    val notes: String? = null,
    val studentId: String? = null,
    val motivation: String? = null,
    val isNotified: Boolean = false
)