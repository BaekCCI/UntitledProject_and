package com.baek.untitledproject.ui.setting.report

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baek.untitledproject.domain.data.Report
import com.baek.untitledproject.domain.repository.ReportRepository
import com.baek.untitledproject.domain.repository.SessionRepository
import com.baek.untitledproject.domain.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportListViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val reportRepository: ReportRepository
) : ViewModel() {

    private val _reportList = MutableStateFlow<Result<List<Report>>>(Result.None)
    val reportList: StateFlow<Result<List<Report>>> = _reportList

    init {
        getReports()
    }
    fun getReports() {
        viewModelScope.launch {
            _reportList.value = Result.Loading
            val userId = sessionRepository.currentUid()
            if (userId == null) {
                _reportList.value = Result.Error("로그인 후 이용해주세요.")
                return@launch
            }

            val result = reportRepository.getReportList(userId)
            _reportList.value = result
            Log.d("ReportListViewModel", result.toString())

        }
    }
}