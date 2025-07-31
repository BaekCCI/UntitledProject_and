package com.baek.untitledproject.ui.recruit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baek.untitledproject.domain.data.AppliedRecruitSummary
import com.baek.untitledproject.domain.data.MyRecruitSummary
import com.baek.untitledproject.domain.data.ScheduleGroupSummary
import com.baek.untitledproject.domain.repository.MyRecruitsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyRecruitsViewModel @Inject constructor(
    private val myRecruitsRepository: MyRecruitsRepository
) : ViewModel() {

    // 다가오는 일정
    private val _scheduleGroups = MutableStateFlow<List<ScheduleGroupSummary>>(emptyList())
    val scheduleGroups: StateFlow<List<ScheduleGroupSummary>> = _scheduleGroups

    // 내가 올린 공고
    private val _myRecruits = MutableStateFlow<List<MyRecruitSummary>>(emptyList())
    val myRecruits: StateFlow<List<MyRecruitSummary>> = _myRecruits

    // 내가 지원한 공고
    private val _appliedRecruits = MutableStateFlow<List<AppliedRecruitSummary>>(emptyList())
    val appliedRecruits: StateFlow<List<AppliedRecruitSummary>> = _appliedRecruits

    // 로딩 상태
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadAllData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 모든 데이터를 병렬로 로드
                _scheduleGroups.value = myRecruitsRepository.getScheduleGroups()
                _myRecruits.value = myRecruitsRepository.getMyRecruits()
                _appliedRecruits.value = myRecruitsRepository.getAppliedRecruits()
            } catch (e: Exception) {
                // TODO: 에러 처리
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // 일정 그룹 펼치기/접기
    fun toggleScheduleGroup(groupId: String) {
        _scheduleGroups.value = _scheduleGroups.value.map { group ->
            if (group.id == groupId) {
                group.copy(isExpanded = !group.isExpanded)
            } else {
                group
            }
        }
    }

    // 내 공고 관리 액션들
    fun onInterviewManageClick(recruitId: String) {
        // TODO: 면접 예약 관리 화면으로 이동
    }

    fun onApplicantManageClick(recruitId: String) {
        // TODO: 지원자 관리 화면으로 이동
    }

    fun onPostManageClick(recruitId: String) {
        // TODO: 작성글 관리 화면으로 이동
    }

    // 지원한 공고 액션들
    fun onInterviewReserveClick(recruitId: String) {
        // TODO: 면접 예약 화면으로 이동
    }

    fun onViewPostClick(recruitId: String) {
        // TODO: 공고글 상세보기로 이동
    }
}