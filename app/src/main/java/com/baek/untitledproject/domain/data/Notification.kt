package com.baek.untitledproject.domain.data

import java.time.LocalDate

data class Notification(
    val id: String,
    val senderUserId: String?,
    val receiverUserId: String,
    val postId: String?,
    val senderOrganization: String,
    val title: String,
    val message: String,
    val notificationType: String,
    val isRead: Boolean,
    val createdAt: Long
)
