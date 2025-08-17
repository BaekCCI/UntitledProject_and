package com.baek.untitledproject.domain.data

import java.time.LocalDateTime

data class Application(
    val applicationId: String,
    val applicantUserId: String,
    val postId: String,
    val status: String,
    val isPassed: Boolean?,
    val interviewSlotId: String?,
    val interviewReservationStatus: String?,

    // 지원자 정보
    val applicantName: String,
    val applicantBirthYear: Int?,
    val applicantGender: String?,
    val applicantDepartment: String?,
    val applicantStudentId: String?,
    val applicantPhone: String?,

    // 공고 정보
    val postTitle: String,
    val postOrganization: String,
    val postAuthorUserId: String,

    // 커스텀 질문 답변
    val customQuestionAnswers: List<QuestionAnswer> = emptyList(),

    val appliedAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)
