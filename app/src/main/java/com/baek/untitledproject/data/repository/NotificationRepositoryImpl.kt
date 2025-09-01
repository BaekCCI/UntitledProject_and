package com.baek.untitledproject.data.repository

import android.util.Log
import com.baek.untitledproject.data.model.mapper.toDomain
import com.baek.untitledproject.data.remote.NotificationRemote
import com.baek.untitledproject.data.sample.NotificationSampleData
import com.baek.untitledproject.domain.data.Notification
import com.baek.untitledproject.domain.repository.NotificationRepository
import com.baek.untitledproject.domain.utils.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val notificationRemote: NotificationRemote
) : NotificationRepository {


    override fun hasNewNotification(userId: String): Flow<Boolean> {
        return notificationRemote.hasNewNotification(userId)
    }

    override suspend fun getNotifications(userId: String): Result<List<Notification>> {
        return try {
            val notifications = notificationRemote.getNotifications(userId).map { it.toDomain() }
            Result.Success(notifications)
        } catch (e: Exception) {
            Log.e("NotificationRepository", "알림 로딩 실패", e)
            Result.Error(" 알림을 불러오는데 실패하였습니다.", e)
        }
    }

    override suspend fun markNotificationsAsRead(userId: String) {
        try {
            notificationRemote.markAllAsRead(userId)
        } catch (e: Exception) {
            Log.e("NotificationRepository", "알림 읽음 표시 실패", e)
        }
    }
}