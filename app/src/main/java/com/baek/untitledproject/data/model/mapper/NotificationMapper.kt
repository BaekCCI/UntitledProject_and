package com.baek.untitledproject.data.model.mapper

import com.baek.untitledproject.data.model.NotificationResponse
import com.baek.untitledproject.domain.data.Notification
import com.google.firebase.Timestamp
import java.util.Date


fun NotificationResponse.toDomain(): Notification {
    return Notification(
        id = notification_id,
        senderUserId = sender_user_id,
        receiverUserId = receiver_user_id,
        postId = post_id,
        senderOrganization = sender_organization,
        title = title,
        message = message,
        notificationType = notification_type,
        isRead = is_read,
        createdAt = created_at.toEpochMillis()
    )
}

fun Timestamp?.toEpochMillis(): Long = this?.toDate()?.time ?: 0L
fun Long.toTimestamp(): Timestamp = Timestamp(Date(this))