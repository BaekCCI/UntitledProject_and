package com.baek.untitledproject.data.model.mapper

import android.net.Uri
import com.baek.untitledproject.common.utils.toLocalDate
import com.baek.untitledproject.data.model.InterviewSlotResponse
import com.baek.untitledproject.data.model.PostImageResponse
import com.baek.untitledproject.data.model.PostResponse
import com.baek.untitledproject.domain.data.Post
import androidx.core.net.toUri
import com.baek.untitledproject.data.model.CustomQuestionResponse
import java.time.LocalDate
import java.time.ZoneId

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
