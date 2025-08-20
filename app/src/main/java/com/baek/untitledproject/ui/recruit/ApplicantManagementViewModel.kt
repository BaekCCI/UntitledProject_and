package com.baek.untitledproject.ui.recruit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baek.untitledproject.domain.data.ApplicantSummary
import com.baek.untitledproject.domain.repository.ApplicantRepository
import com.baek.untitledproject.domain.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ApplicantManagementViewModel @Inject constructor(
    private val applicantRepository: ApplicantRepository
) : ViewModel() {

    // 지원자 목록
    private val _applicants = MutableStateFlow<List<ApplicantSummary>>(emptyList())
    val applicants: StateFlow<List<ApplicantSummary>> = _applicants

    // 필터링된 지원자 목록
    private val _filteredApplicants = MutableStateFlow<List<ApplicantSummary>>(emptyList())
    val filteredApplicants: StateFlow<List<ApplicantSummary>> = _filteredApplicants

    // 현재 선택된 필터
    private val _currentFilter = MutableStateFlow("all")
    val currentFilter: StateFlow<String> = _currentFilter

    // 로딩 상태
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // 에러 메시지
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // 전체 지원자 목록 (필터링 전)
    private var allApplicants: List<ApplicantSummary> = emptyList()

    // 현재 공고 ID 저장
    private var currentRecruitId: String = ""

    fun loadApplicants(recruitId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                currentRecruitId = recruitId
                allApplicants = applicantRepository.getApplicants(recruitId)
                allApplicants.forEach { applicant ->
                }
                _applicants.value = allApplicants
                applyCurrentFilter()
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "지원자 목록을 불러오는데 실패했습니다."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun filterApplicants(filter: String) {
        _currentFilter.value = filter
        applyCurrentFilter()
    }

    private fun applyCurrentFilter() {
        val filtered = when (_currentFilter.value) {
            "all" -> allApplicants.filter { it.status == "지원서 제출됨" }
            "interview" -> allApplicants.filter { it.status == "면접 대기 중" }
            "review" -> allApplicants.filter { it.status == "심사 대기 중" }
            "complete" -> allApplicants.filter { it.status == "심사 완료됨" }
            else -> allApplicants
        }
        _applicants.value = filtered
    }

    // 면접 제안 (면접 일정 체크 포함)
    fun scheduleInterviews(applicantIds: List<String>) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                when (val result = applicantRepository.scheduleInterviews(applicantIds)) {
                    is Result.Success -> {
                        // 성공 시 데이터 새로고침
                        loadApplicants(currentRecruitId)
                    }
                    is Result.Error -> {
                        _errorMessage.value = result.message
                    }
                    is Result.Loading -> {
                        // 이미 _isLoading으로 처리중
                    }
                    is Result.None -> {
                        // 초기 상태, 아무것도 하지 않음
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "면접 제안 중 오류가 발생했습니다."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun completeInterviews(applicantIds: List<String>) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                when (val result = applicantRepository.completeInterviews(applicantIds)) {
                    is Result.Success -> {
                        loadApplicants(currentRecruitId)
                    }
                    is Result.Error -> {
                        _errorMessage.value = result.message
                    }
                    is Result.Loading -> {
                        // 이미 _isLoading으로 처리중
                    }
                    is Result.None -> {
                        // 초기 상태, 아무것도 하지 않음
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "면접 완료 처리 중 오류가 발생했습니다."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun passApplicants(applicantIds: List<String>) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                when (val result = applicantRepository.passApplicants(applicantIds)) {
                    is Result.Success -> {
                        loadApplicants(currentRecruitId)
                    }
                    is Result.Error -> {
                        _errorMessage.value = result.message
                    }
                    is Result.Loading -> {
                        // 이미 _isLoading으로 처리중
                    }
                    is Result.None -> {
                        // 초기 상태, 아무것도 하지 않음
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "합격 처리 중 오류가 발생했습니다."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun failApplicants(applicantIds: List<String>) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                when (val result = applicantRepository.failApplicants(applicantIds)) {
                    is Result.Success -> {
                        loadApplicants(currentRecruitId)
                    }
                    is Result.Error -> {
                        _errorMessage.value = result.message
                    }
                    is Result.Loading -> {
                        // 이미 _isLoading으로 처리중
                    }
                    is Result.None -> {
                        // 초기 상태, 아무것도 하지 않음
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "불합격 처리 중 오류가 발생했습니다."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun notifyResults(applicantIds: List<String>) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                when (val result = applicantRepository.notifyResults(applicantIds)) {
                    is Result.Success -> {
                        // 알림 완료 후 특별한 처리가 필요하다면 추가
                    }
                    is Result.Error -> {
                        _errorMessage.value = result.message
                    }
                    is Result.Loading -> {
                        // 이미 _isLoading으로 처리중
                    }
                    is Result.None -> {
                        // 초기 상태, 아무것도 하지 않음
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "알림 발송 중 오류가 발생했습니다."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun notifyAllApplicants() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val allIds = _applicants.value.map { it.id }
                when (val result = applicantRepository.notifyResults(allIds)) {
                    is Result.Success -> {
                        // 전체 알림 완료
                    }
                    is Result.Error -> {
                        _errorMessage.value = result.message
                    }
                    is Result.Loading -> {
                        // 이미 _isLoading으로 처리중
                    }
                    is Result.None -> {
                        // 초기 상태, 아무것도 하지 않음
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "전체 알림 발송 중 오류가 발생했습니다."
            } finally {
                _isLoading.value = false
            }
        }
    }

    // 에러 메시지 초기화
    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    // 현재 공고 ID 반환
    private fun getCurrentRecruitId(): String {
        return currentRecruitId
    }
}