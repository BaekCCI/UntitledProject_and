package com.baek.untitledproject.ui.board.notification

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baek.untitledproject.domain.data.Notification
import com.baek.untitledproject.domain.repository.NotificationRepository
import com.baek.untitledproject.domain.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import com.baek.untitledproject.domain.utils.Result
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val sessionRepository: SessionRepository
) : ViewModel() {
    private val _notifications = MutableStateFlow<Result<List<Notification>>>(Result.Loading)
    val notifications: StateFlow<Result<List<Notification>>> = _notifications

    private val userId = sessionRepository.currentUid()

    fun load() {
        viewModelScope.launch {
            _notifications.value = Result.Loading
            if (userId == null) {
                _notifications.value = Result.None
                return@launch
            }
            val result = notificationRepository.getNotifications(userId)
            _notifications.value = result

            //서버 값 is_read = true로 변경
            if (result is Result.Success) {
                notificationRepository.markNotificationsAsRead(userId)
            }
        }
    }
}