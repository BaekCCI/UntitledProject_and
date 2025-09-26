package com.baek.untitledproject.ui.board.report

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baek.untitledproject.domain.data.Report
import com.baek.untitledproject.domain.repository.SessionRepository
import com.baek.untitledproject.domain.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val sessionRepository: SessionRepository
) : ViewModel() {

    private val reportContent = MutableStateFlow(Report())

    private val _state = MutableStateFlow<Result<Unit>>(Result.None)
    val state: StateFlow<Result<Unit>> = _state

    private val _reportState = MutableStateFlow<Result<String>>(Result.None)
    val reportState: StateFlow<Result<String>> = _reportState

    fun initInfo(topic: ReportTopic, targetId: String, reportedUserId: String) {
        Log.d("ReportViewModel", "$topic, $targetId")

        viewModelScope.launch {
            _state.value = Result.Loading
            val userId = sessionRepository.currentUid()
            if (userId == null) { //로그아웃 상태이면
                _state.value = Result.Error("로그인 후 이용해주세요.")
                return@launch
            }

            //TODO: 타입
            when (topic) {
                ReportTopic.POST -> initForPostReport(userId, targetId, reportedUserId)
                ReportTopic.CONVERSATION -> initForConversationReport(
                    userId,
                    targetId,
                    reportedUserId
                )

                ReportTopic.MESSAGE -> initForMessageReport(userId, targetId, reportedUserId)
            }
            _state.value = Result.Success(Unit)

        }
    }

    private fun initForPostReport(userId: String, postId: String, reportedUserId: String) {
        reportContent.update {
            it.copy(
                reporterUserId = userId,
                reportedUserId = reportedUserId,
                postId = postId
            )
        }
    }

    private fun initForConversationReport(
        userId: String,
        conversationId: String,
        reportedUserId: String
    ) {

        reportContent.update {
            it.copy(
                reporterUserId = userId,
                reportedUserId = reportedUserId,
                conversationId = conversationId
            )
        }
    }

    private fun initForMessageReport(
        userId: String,
        messageId: String,
        reportedUserId: String
    ) {
        reportContent.update {
            it.copy(
                reporterUserId = userId,
                reportedUserId = reportedUserId,
                messageId = messageId
            )
        }
    }

    fun saveType(type: String) {
        reportContent.update {
            it.copy(
                reportType = type
            )
        }
    }

    fun sendReport(content: String) {
        viewModelScope.launch {
            _reportState.value = Result.Loading
            reportContent.update {
                it.copy(
                    reason = content
                )
            }
            _reportState.value = Result.Success("D")
            //TODO: 서버에 저장
        }
    }
}
