package com.baek.untitledproject.domain.data

import com.baek.untitledproject.common.utils.toLocalDate
import com.baek.untitledproject.data.model.InterviewSlotResponse
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * 면접 시간대 도메인 모델
 */
data class InterviewSlot(
    val slotId: String,
    val postId: String,
    val interviewDate: LocalDate,
    val interviewTime: LocalTime,
    val maxCapacity: Int,
    val currentReservations: Int,
    val isAvailable: Boolean = currentReservations < maxCapacity
) {

    val remainingCapacity: Int
        get() = maxCapacity - currentReservations

    val timeText: String
        get() = interviewTime.toString()

    val dateText: String
        get() = "${interviewDate.monthValue}월 ${interviewDate.dayOfMonth}일"

    val capacityText: String
        get() = "${currentReservations}/${maxCapacity}명 예약됨"
}

fun InterviewSlotResponse.toDomain(slotId: String, postId: String): InterviewSlot? {
    val date = interview_date?.toLocalDate() ?: return null
    val time = try {
        LocalTime.parse(interview_time, DateTimeFormatter.ofPattern("HH:mm"))
    } catch (e: Exception) {
        return null
    }

    return InterviewSlot(
        slotId = slotId,
        postId = postId,
        interviewDate = date,
        interviewTime = time,
        maxCapacity = max_capacity,
        currentReservations = current_reservations
    )
}

fun com.google.firebase.firestore.DocumentSnapshot.toInterviewSlot(postId: String): InterviewSlot? {
    val response = this.toObject(InterviewSlotResponse::class.java) ?: return null
    return response.toDomain(this.id, postId)
}