package com.baek.untitledproject.data.model.mapper

import android.net.Uri
import com.baek.untitledproject.common.utils.toLocalDate
import com.baek.untitledproject.data.model.InterviewSlotResponse
import com.baek.untitledproject.data.model.PostResponse
import com.baek.untitledproject.domain.data.Post
import com.baek.untitledproject.common.utils.toTimestamp
import com.baek.untitledproject.data.model.CustomQuestionResponse
import com.baek.untitledproject.domain.data.ApplicationRequirements
import com.baek.untitledproject.domain.data.CustomQuestion
import com.google.firebase.Timestamp
import java.time.LocalDate


fun PostResponse.toDomain(
    images: List<Uri>,
    interviewSlots: List<InterviewSlotResponse>,
    customQuestions: List<CustomQuestionResponse>
): Post {
    val sortedInterviewSlots = interviewSlots.toInterviewSlotMap()
    val maxCapacity = interviewSlots.firstOrNull()?.max_capacity ?: 1

    return Post(
        postId = post_id,
        title = title,
        organization = organization,
        content = content,
        recruitmentStart = recruitment_start?.toLocalDate(),
        recruitmentEnd = recruitment_end?.toLocalDate(),
        imageUris = images,

        hasInterview = has_interview,
        interviewSlot = sortedInterviewSlots,
        interviewStart = sortedInterviewSlots.earliestInterviewDate(),
        interviewEnd = sortedInterviewSlots.latestInterviewDate(),
        interviewLocation = interview_location,
        maxCapacity = maxCapacity,

        requiresName = requires_name,
        requiresStudentId = requires_student_id,
        requiresDepartment = requires_department,
        requiresGender = requires_gender,
        requiresAge = requires_age,
        //requiresPhone = requires_phone,

        customQuestions = customQuestions.toList()
    )
}

//submitPost용(수정시엔 사용 X)
fun Post.toResponse(
    postId: String,
    now: Timestamp
): PostResponse {

    return PostResponse(
        post_id = postId,
        author_user_id = "text_and1",
        title = title ?: "",
        organization = organization ?: "",
        content = content ?: "",
        recruitment_start = recruitmentStart?.toTimestamp(),
        recruitment_end = recruitmentEnd?.toTimestamp(),

        has_interview = hasInterview ?: false,
        interview_location = interviewLocation,
        status = "recruiting",

        requires_name = requiresName,
        requires_student_id = requiresStudentId,
        requires_department = requiresDepartment,
        requires_gender = requiresGender,
        requires_age = requiresAge,
        //requires_phone = requiresPhone,

        author_name = "Ash",
        author_organization = organization ?: "",

        created_at = now
    )
}

fun List<InterviewSlotResponse>.toInterviewSlotMap(): Map<LocalDate, List<String>> {
    return this
        .filter { it.interview_date != null && it.interview_time.isNotBlank() }
        .groupBy(
            keySelector = { it.interview_date?.toLocalDate()!! }, // Timestamp
            valueTransform = { it.interview_time }
        )
        .mapValues { (_, times) -> times.distinct().sorted() } // 중복 제거 후 정렬
        .toSortedMap(compareBy { it })
}

fun List<CustomQuestionResponse>.toList(): List<String> {
    return this
        .sortedBy { it.question_order }
        .map { it.question_text }
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