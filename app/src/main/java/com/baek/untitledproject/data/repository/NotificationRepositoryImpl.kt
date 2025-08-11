package com.baek.untitledproject.data.repository

import android.util.Log
import com.baek.untitledproject.data.model.mapper.toDomain
import com.baek.untitledproject.data.sample.NotificationSampleData
import com.baek.untitledproject.domain.data.Notification
import com.baek.untitledproject.domain.repository.NotificationRepository
import com.baek.untitledproject.domain.utils.Result
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor() : NotificationRepository {
    override suspend fun getNotifications(userId: String): Result<List<Notification>> {
        return try {
            val notifications = NotificationSampleData.notiList
            val result = notifications.filter { it.receiver_user_id == userId }.map{it.toDomain()}
            Result.Success(result)
        } catch (e: Exception) {
            Log.e("NotificationRepository", "알림 로딩 실패", e)
            Result.Error(" 알림을 불러오는데 실패하였습니다.", e)
        }
    }
}