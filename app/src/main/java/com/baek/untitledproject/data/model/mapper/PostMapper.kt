package com.baek.untitledproject.data.model.mapper

import android.net.Uri
import com.baek.untitledproject.common.utils.toLocalDate
import com.baek.untitledproject.data.model.InterviewSlotResponse
import com.baek.untitledproject.data.model.PostImageResponse
import com.baek.untitledproject.data.model.PostResponse
import com.baek.untitledproject.domain.data.Post
import androidx.core.net.toUri
import com.baek.untitledproject.data.model.CustomQuestionResponse
import com.baek.untitledproject.domain.data.MyRecruitSummary
import com.baek.untitledproject.domain.data.ApplicationRequirements
import com.baek.untitledproject.domain.data.CustomQuestion
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun PostResponse.toDomain(
    images: List<PostImageResponse>,
    interviewSlots: List<InterviewSlotResponse>,
    customQuestions: List<CustomQuestionResponse>
): Post {
    val sortedImages: List<Uri> = images.sortedBy { it.image_order }.map { it.image_url.toUri() }
    val sortedCustomQuestions: List<String> =
        customQuestions.sortedBy { it.question_order }.map { it.question_text }


    return Post(
        postId = post_id,
        title = title,
        organization = organization,
        content = content,
        recruitmentStart = recruitment_start?.toLocalDate(),
        recruitmentEnd = recruitment_end?.toLocalDate(),
        imageUris = sortedImages,

        hasInterview = has_interview,
        interviewSlot = interviewSlots.toInterviewSlotMap(),
        interviewStart = interviewSlots.earliestInterviewDate(),
        interviewEnd = interviewSlots.latestInterviewDate(),
        interviewLocation = interview_location,

        requiresName = requires_name,
        requiresStudentId = requires_student_id,
        requiresDepartment = requires_department,
        requiresGender = requires_gender,
        requiresAge = requires_age,
        requiresPhone = requires_phone,

        customQuestions = sortedCustomQuestions
    )
}

fun List<InterviewSlotResponse>.toInterviewSlotMap(): Map<LocalDate, String> {
    return this
        .filter { it.interview_date != null }
        .associate { slot ->
            val localDate = slot.interview_date!!.toDate()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            localDate to slot.interview_time
        }
}

fun List<InterviewSlotResponse>.earliestInterviewDate(): LocalDate? =
    asSequence().mapNotNull { it.interview_date?.toLocalDate() }.minOrNull()

fun List<InterviewSlotResponse>.latestInterviewDate(): LocalDate? =
    asSequence().mapNotNull { it.interview_date?.toLocalDate() }.maxOrNull()

fun PostResponse.toApplicationRequirement(customQuestions: List<CustomQuestionResponse>): ApplicationRequirements {
    return ApplicationRequirements(
        requiresName = requires_name,
        requiresStudentId = requires_student_id,
        requiresDepartment = requires_department,
        requiresGender = requires_gender,
        requiresAge = requires_gender,
        //requiresPhone = requires_phone,
        customQuestions = customQuestions.sortedBy { it.question_order }
            .map { CustomQuestion(questionId = it.question_id, questionText = it.question_text) }
    )
}

fun PostResponse.toMyRecruitSummary(
    thumbnailUrl: String? = null,
    applicantCount: Int = 0
): MyRecruitSummary {
    return MyRecruitSummary(
        id = post_id,
        title = title,
        category = organization,
        recruitStatus = mapPostStatusToText(status),
        thumbnailUrl = thumbnailUrl,
        hasInterview = has_interview,
        applicantCount = applicantCount,
        recruitmentEnd = recruitment_end?.toLocalDate()?.format(
            DateTimeFormatter.ofPattern("MM/dd")
        )
    )
}

// 공고 상태를 한글로 변환
private fun mapPostStatusToText(status: String): String {
    return when (status) {
        "recruiting" -> "모집중"
        "completed" -> "모집완료"
        else -> "알 수 없음"
    }
}