package com.baek.untitledproject.domain.repository

import com.baek.untitledproject.domain.data.Notification
import com.baek.untitledproject.domain.utils.Result

interface NotificationRepository {
    suspend fun getNotifications(userId: String): Result<List<Notification>>
}