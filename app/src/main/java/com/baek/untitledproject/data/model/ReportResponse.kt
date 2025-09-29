package com.baek.untitledproject.data.model

import com.google.firebase.Timestamp

data class ReportResponse(
    val report_id: String = "",
    val reporter_user_id: String = "",
    val reported_user_id: String = "",
    val post_id: String? = null,
    val conversation_id: String? = null,
    val message_id: String? = null,
    val report_type: String = "",
    val reason: String? = null,
    val status: String = "pending",
    val created_at: Timestamp? = null
)