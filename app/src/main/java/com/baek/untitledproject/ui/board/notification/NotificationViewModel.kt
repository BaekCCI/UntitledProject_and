package com.baek.untitledproject.ui.board.notification

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baek.untitledproject.domain.data.Notification
import com.baek.untitledproject.domain.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import com.baek.untitledproject.domain.utils.Result
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class NotificationViewModel @Inject constructor(
    val repository: NotificationRepository
) :ViewModel(){
    private val _notifications = MutableStateFlow<Result<List<Notification>>>(Result.Loading)
    val notifications: StateFlow<Result<List<Notification>>> = _notifications

    fun load(){
        viewModelScope.launch {
            _notifications.value = Result.Loading
            val result = repository.getNotifications("user1")
            Log.d("NotificationViewModel","load 결과 = $result")
            _notifications.value = result
        }
    }
}