package com.baek.untitledproject.ui.recruit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baek.untitledproject.domain.data.ApplicantSummary
import com.baek.untitledproject.domain.repository.ApplicantRepository
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

    // 전체 지원자 목록 (필터링 전)
    private var allApplicants: List<ApplicantSummary> = emptyList()

    fun loadApplicants(recruitId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                allApplicants = applicantRepository.getApplicants(recruitId)
                _applicants.value = allApplicants
                applyCurrentFilter()
            } catch (e: Exception) {
                // TODO: 에러 처리
                e.printStackTrace()
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
            "all" -> allApplicants.filter { it.status == "submitted" }
            "interview" -> allApplicants.filter { it.status == "interview_scheduled" }
            "review" -> allApplicants.filter { it.status == "interview_completed" }
            "complete" -> allApplicants.filter { it.status in listOf("passed", "failed") }
            else -> allApplicants
        }
        _applicants.value = filtered
    }

    // 액션 메서드들
    fun scheduleInterviews(applicantIds: List<String>) {
        viewModelScope.launch {
            try {
                applicantRepository.scheduleInterviews(applicantIds)
                // 데이터 새로고침
                loadApplicants(getCurrentRecruitId())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun completeInterviews(applicantIds: List<String>) {
        viewModelScope.launch {
            try {
                applicantRepository.completeInterviews(applicantIds)
                loadApplicants(getCurrentRecruitId())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun passApplicants(applicantIds: List<String>) {
        viewModelScope.launch {
            try {
                applicantRepository.passApplicants(applicantIds)
                loadApplicants(getCurrentRecruitId())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun failApplicants(applicantIds: List<String>) {
        viewModelScope.launch {
            try {
                applicantRepository.failApplicants(applicantIds)
                loadApplicants(getCurrentRecruitId())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun notifyResults(applicantIds: List<String>) {
        viewModelScope.launch {
            try {
                applicantRepository.notifyResults(applicantIds)
                // 알림 완료 후 특별한 처리가 필요하다면 추가
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun notifyAllApplicants() {
        viewModelScope.launch {
            try {
                val allIds = _applicants.value.map { it.id }
                applicantRepository.notifyResults(allIds)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // TODO: 현재 공고 ID를 저장하고 반환
    private fun getCurrentRecruitId(): String {
        return "recruit_id_placeholder"
    }
}