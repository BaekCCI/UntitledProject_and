package com.baek.untitledproject.domain.repository

import com.baek.untitledproject.domain.data.Notification
import com.baek.untitledproject.domain.utils.Result
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {

    fun hasNewNotification(userId: String) : Flow<Boolean>
    suspend fun getNotifications(userId: String): Result<List<Notification>>

    suspend fun markNotificationsAsRead(userId: String)
}