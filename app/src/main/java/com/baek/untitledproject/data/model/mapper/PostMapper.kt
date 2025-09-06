package com.baek.untitledproject.data.model.mapper

import android.net.Uri
import com.baek.untitledproject.common.utils.toLocalDate
import com.baek.untitledproject.data.model.InterviewSlotResponse
import com.baek.untitledproject.data.model.PostResponse
import com.baek.untitledproject.domain.data.Post
import com.baek.untitledproject.common.utils.toTimestamp
import com.baek.untitledproject.data.model.CustomQuestionResponse
import com.baek.untitledproject.domain.data.MyRecruitSummary
import com.baek.untitledproject.domain.data.ApplicationRequirements
import com.baek.untitledproject.domain.data.CustomQuestion
import com.baek.untitledproject.domain.data.PostRead
import com.baek.untitledproject.domain.data.PostWrite
import com.baek.untitledproject.domain.data.TimeSlot
import com.baek.untitledproject.domain.data.User
import com.google.firebase.Timestamp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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

//submitPost용
fun Post.toResponse(
    postId: String,
    now: Timestamp,
    createdAt: Timestamp,
    isUpdate: Boolean = false
): PostResponse {

    return PostResponse(
        post_id = postId,
        title = title ?: "",
        organization = organization ?: "",
        content = content ?: "",
        recruitment_start = recruitmentStart!!.toTimestamp(),
        recruitment_end = recruitmentEnd!!.toTimestamp(),

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

        created_at = createdAt,
        updated_at = if (isUpdate) now else null
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

// ------리팩 중

//읽기 전용으로 변환
fun PostResponse.toPostRead(
    images: List<Uri>,
    interviewSlots: List<InterviewSlotResponse>
): PostRead {
    return PostRead(
        postId = post_id,
        authorUserId = author_user_id,
        title = title,
        organization = organization,
        content = content,
        recruitmentStart = recruitment_start.toLocalDate(),
        recruitmentEnd = recruitment_end?.toLocalDate(),
        status = status,
        imageUris = images,

        hasInterview = has_interview,
        interviewStart = interviewSlots.getEarliestDate(),
        interviewEnd = interviewSlots.getLatestDate(),
        interviewLocation = interview_location,

        requiresName = requires_name,
        requiresStudentId = requires_student_id,
        requiresDepartment = requires_department,
        requiresGender = requires_gender,
        requiresAge = requires_age,
        //requiresPhone = requires_phone
    )
}

fun List<InterviewSlotResponse>.getEarliestDate(): LocalDate? {
    return this.mapNotNull { it.interview_date }.minOrNull()?.toLocalDate()
}

fun List<InterviewSlotResponse>.getLatestDate(): LocalDate? {
    return this.mapNotNull { it.interview_date }.maxOrNull()?.toLocalDate()
}

//게시글 업로드용
fun PostWrite.toResponse(postId: String, user: User, createdAt: Timestamp): PostResponse {
    return PostResponse(
        post_id = postId,
        author_user_id = user.userId!!,
        title = title ?: "",
        organization = organization ?: "",
        content = content ?: "",
        recruitment_start = recruitmentStart?.toTimestamp() ?: createdAt,
        recruitment_end = recruitmentEnd?.toTimestamp(),
        has_interview = hasInterview ?: false,
        interview_location = interviewLocation,
        status = "recruiting",

        requires_name = requiresName,
        requires_student_id = requiresStudentId,
        requires_department = requiresDepartment,
        requires_gender = requiresGender,
        requires_age = requiresAge,

        author_name = user.name,
        author_organization = organization ?: "",

        created_at = createdAt,
        updated_at = createdAt
    )
}

fun TimeSlot.separate(step: Int): List<String> {
    val list = mutableListOf<String>()
    val fmt = DateTimeFormatter.ofPattern("HH:mm")
    var t = start
    while (t.isBefore(end)) {
        list += t.format(fmt)
        t = t.plusMinutes(step.toLong())
    }
    return list
}

// 공고 상태를 한글로 변환
//TODO: 삭제 예정
private fun mapPostStatusToText(status: String): String {
    return when (status) {
        "recruiting" -> "모집중"
        "completed" -> "모집완료"
        else -> "알 수 없음"
    }
}