package com.baek.untitledproject.data.model.mapper

import com.baek.untitledproject.data.model.ApplicationResponse
import com.baek.untitledproject.domain.data.Application
import com.baek.untitledproject.domain.data.ApplicantSummary
import com.baek.untitledproject.domain.data.AppliedRecruitSummary
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar

fun ApplicationResponse.toDomain(): Application {
    return Application(
        applicationId = application_id,
        applicantUserId = applicant_user_id,
        postId = post_id,
        status = status,
        isPassed = is_passed,
        interviewSlotId = interview_slot_id,
        interviewReservationStatus = interview_reservation_status,

        applicantName = applicant_name,
        applicantBirthYear = applicant_birth_year,
        applicantGender = applicant_gender,
        applicantDepartment = applicant_department,
        applicantStudentId = applicant_student_id,
        applicantPhone = applicant_phone,

        postTitle = post_title,
        postOrganization = post_organization,
        postAuthorUserId = post_author_user_id,

        appliedAt = applied_at?.toLocalDateTime(),
        updatedAt = updated_at?.toLocalDateTime()
    )
}

// ApplicationResponse -> ApplicantSummary
fun ApplicationResponse.toApplicantSummary(): ApplicantSummary {
    return ApplicantSummary(
        id = application_id,
        name = applicant_name,
        gender = applicant_gender ?: "미정",
        age = calculateAge(applicant_birth_year),
        department = applicant_department,
        studentId = applicant_student_id,
        phoneNumber = applicant_phone,
        status = status,
        isPassed = is_passed,
        statusText = mapStatusToText(status, is_passed)
    )
}

// ApplicationResponse -> AppliedRecruitSummary (지원한 공고 목록용)
fun ApplicationResponse.toAppliedRecruitSummary(
    recruitStatus: String,
    hasInterview: Boolean
): AppliedRecruitSummary {
    return AppliedRecruitSummary(
        id = application_id,
        postId = post_id,
        title = post_title,
        category = post_organization,
        recruitStatus = mapRecruitStatusToText(recruitStatus),
        applicationStatus = mapStatusToText(status, is_passed),
        hasInterview = hasInterview,
        interviewSlotId = interview_slot_id,
        canReserveInterview = canReserveInterview(status, hasInterview, interview_slot_id)
    )
}

// 헬퍼 함수들
private fun calculateAge(birthYear: Int?): Int {
    if (birthYear == null) return 0
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    return currentYear - birthYear + 1 // 한국식 나이
}

private fun mapStatusToText(status: String, isPassed: Boolean?): String {
    return when {
        status == "submitted" -> "지원서 제출 완료"
        status == "interview_waiting" -> "면접 대기중"
        status == "review_waiting" -> "심사 대기중"
        status == "review_completed" && isPassed == true -> "합격"
        status == "review_completed" && isPassed == false -> "불합격"
        status == "review_completed" && isPassed == null -> "심사 완료"
        else -> "알 수 없음"
    }
}

private fun mapRecruitStatusToText(status: String): String {
    return when (status) {
        "recruiting" -> "모집중"
        "completed" -> "모집완료"
        else -> "알 수 없음"
    }
}

private fun canReserveInterview(
    status: String,
    hasInterview: Boolean,
    interviewSlotId: String?
): Boolean {
    return hasInterview && status == "interview_waiting" && interviewSlotId == null
}

// Timestamp -> LocalDateTime 변환
private fun com.google.firebase.Timestamp.toLocalDateTime(): LocalDateTime {
    return this.toDate().toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()
}