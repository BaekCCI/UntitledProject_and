package com.baek.untitledproject.data.model

import com.google.firebase.Timestamp

data class NotificationResponse (
    val notification_id: String = "",
    val sender_user_id: String? = null,
    val receiver_user_id: String = "",
    val post_id: String? = null,
    val sender_organization: String = "",
    val title: String = "",
    val message: String = "",
    val notification_type: String = "",
    val is_read: Boolean = false,
    val created_at: Timestamp? = null
)