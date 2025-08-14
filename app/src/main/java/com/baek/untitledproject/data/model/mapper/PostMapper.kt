package com.baek.untitledproject.data.model.mapper

import android.net.Uri
import com.baek.untitledproject.common.utils.toLocalDate
import com.baek.untitledproject.data.model.InterviewSlotResponse
import com.baek.untitledproject.data.model.PostImageResponse
import com.baek.untitledproject.data.model.PostResponse
import com.baek.untitledproject.domain.data.Post
import androidx.core.net.toUri
import com.baek.untitledproject.data.model.CustomQuestionResponse
import com.baek.untitledproject.domain.data.ApplicationRequirements
import com.baek.untitledproject.domain.data.CustomQuestion
import java.time.LocalDate
import java.time.ZoneId


fun PostResponse.toDomain(
    images: List<Uri>,
    interviewSlots: Map<LocalDate, List<String>>,
    customQuestions: List<String>
): Post {

    return Post(
        postId = post_id,
        title = title,
        organization = organization,
        content = content,
        recruitmentStart = recruitment_start?.toLocalDate(),
        recruitmentEnd = recruitment_end?.toLocalDate(),
        imageUris = images,

        hasInterview = has_interview,
        interviewSlot = interviewSlots,
        interviewStart = interviewSlots.earliestInterviewDate(),
        interviewEnd = interviewSlots.latestInterviewDate(),
        interviewLocation = interview_location,

        requiresName = requires_name,
        requiresStudentId = requires_student_id,
        requiresDepartment = requires_department,
        requiresGender = requires_gender,
        requiresAge = requires_age,
        requiresPhone = requires_phone,

        customQuestions = customQuestions
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

fun Map<LocalDate, List<String>>.earliestInterviewDate(): LocalDate? =
    asSequence().map { it.key }.minOrNull()

fun Map<LocalDate, List<String>>.latestInterviewDate(): LocalDate? =
    asSequence().map { it.key }.maxOrNull()

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