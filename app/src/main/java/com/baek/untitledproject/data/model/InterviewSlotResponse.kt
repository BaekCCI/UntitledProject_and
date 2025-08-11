package com.baek.untitledproject.data.model

import com.google.firebase.Timestamp

data class InterviewSlotResponse(
    val interview_date : Timestamp? = null,
    val interview_time : String = "",
    val max_capacity : Int = 0,
    val current_reservations : Int = 0
)