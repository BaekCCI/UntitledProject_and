package com.baek.untitledproject.ui.recruit

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baek.untitledproject.domain.data.InterviewSlot
import com.baek.untitledproject.domain.repository.MyRecruitsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class InterviewReservationViewModel @Inject constructor(
    private val myRecruitsRepository: MyRecruitsRepository
) : ViewModel() {

    // UI 상태
    private val _uiState = MutableStateFlow(InterviewReservationUiState())
    val uiState: StateFlow<InterviewReservationUiState> = _uiState.asStateFlow()

    // 전체 면접 시간대 목록
    private val _allInterviewSlots = MutableStateFlow<List<InterviewSlot>>(emptyList())
    val allInterviewSlots: StateFlow<List<InterviewSlot>> = _allInterviewSlots.asStateFlow()

    // 선택된 날짜의 시간대 목록
    private val _selectedDateSlots = MutableStateFlow<List<InterviewSlot>>(emptyList())
    val selectedDateSlots: StateFlow<List<InterviewSlot>> = _selectedDateSlots.asStateFlow()

    // 선택된 시간대
    private val _selectedSlot = MutableStateFlow<InterviewSlot?>(null)
    val selectedSlot: StateFlow<InterviewSlot?> = _selectedSlot.asStateFlow()

    private var postId: String? = null
    private var applicationId: String? = null

    /**
     * 초기화 - Activity에서 호출
     */
    fun initialize(postId: String, applicationId: String) {
        this.postId = postId
        this.applicationId = applicationId
        loadInterviewSlots()
    }

    /**
     * 면접 시간대 목록 조회
     */
    private fun loadInterviewSlots() {
        val currentPostId = postId ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val slots = myRecruitsRepository.getAvailableInterviewSlots(currentPostId)

                _allInterviewSlots.value = slots

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "면접 시간대를 불러올 수 없습니다."
                )
            }
        }
    }

    /**
     * 날짜 선택
     */
    fun selectDate(date: LocalDate) {
        // 해당 날짜의 시간대 필터링
        val slotsForDate = _allInterviewSlots.value.filter { slot ->
            slot.interviewDate == date
        }.sortedBy { it.interviewTime }

        _selectedDateSlots.value = slotsForDate
        _selectedSlot.value = null // 시간대 선택 초기화

        val newState = _uiState.value.copy(
            selectedDate = date,
            showTimeSlots = true,
            showReserveButton = false
        )
        _uiState.value = newState
    }

    /**
     * 시간대 선택
     */
    fun selectTimeSlot(slot: InterviewSlot) {
        _selectedSlot.value = slot
        _uiState.value = _uiState.value.copy(
            showReserveButton = true
        )
    }

    /**
     * 면접 예약하기
     */
    fun reserveInterview() {
        val currentApplicationId = applicationId ?: return
        val currentSlot = _selectedSlot.value ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isReserving = true)

            try {
                val success = myRecruitsRepository.reserveInterviewSlot(
                    applicationId = currentApplicationId,
                    slotId = currentSlot.slotId
                )

                if (success) {
                    _uiState.value = _uiState.value.copy(
                        isReserving = false,
                        reservationSuccess = true,
                        errorMessage = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isReserving = false,
                        errorMessage = "예약에 실패했습니다."
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isReserving = false,
                    errorMessage = e.message ?: "예약 중 오류가 발생했습니다."
                )
            }
        }
    }

    /**
     * 에러 메시지 초기화
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    /**
     * 예약 성공 상태 초기화 (Activity에서 finish() 전 호출)
     */
    fun clearReservationSuccess() {
        _uiState.value = _uiState.value.copy(reservationSuccess = false)
    }

    /**
     * 날짜에 면접 시간대가 있는지 확인
     */
    fun hasTimeSlotsForDate(date: LocalDate): Boolean {
        return _allInterviewSlots.value.any { it.interviewDate == date }
    }

    fun clearTimeSlotSelection() {
        _selectedSlot.value = null
        _uiState.value = _uiState.value.copy(
            showReserveButton = false
        )
    }

    fun clearDateSelection() {
        _selectedDateSlots.value = emptyList()
        _selectedSlot.value = null // 시간대 선택도 함께 해제

        _uiState.value = _uiState.value.copy(
            selectedDate = null,
            showTimeSlots = false,
            showReserveButton = false
        )
    }
}

/**
 * UI 상태를 관리하는 데이터 클래스
 */
data class InterviewReservationUiState(
    val isLoading: Boolean = false,
    val isReserving: Boolean = false,
    val selectedDate: LocalDate? = null,
    val showTimeSlots: Boolean = false,
    val showReserveButton: Boolean = false,
    val reservationSuccess: Boolean = false,
    val errorMessage: String? = null
)