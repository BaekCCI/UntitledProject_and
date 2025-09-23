package com.baek.untitledproject.data.model

import com.google.firebase.Timestamp

data class InterviewSlotResponse(
    val slot_id: String? = null,
    val post_id: String? = null,
    val interview_date: Timestamp? = null,
    val interview_time: String = "",
    val max_capacity: Int = 0,
    val current_reservations: Int = 0,
    val duration: Int = 30
)