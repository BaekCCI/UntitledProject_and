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
class ApplicantDetailViewModel @Inject constructor(
    private val applicantRepository: ApplicantRepository
) : ViewModel() {

    // 지원자 상세 정보
    private val _applicantDetail = MutableStateFlow<ApplicantSummary?>(null)
    val applicantDetail: StateFlow<ApplicantSummary?> = _applicantDetail

    // 액션 버튼들
    private val _actionButtons = MutableStateFlow<List<List<ApplicantActionButton>>>(emptyList())
    val actionButtons: StateFlow<List<List<ApplicantActionButton>>> = _actionButtons

    // 로딩 상태
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadApplicantDetail(applicantId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val applicant = applicantRepository.getApplicantDetail(applicantId)
                _applicantDetail.value = applicant
                updateActionButtons(applicant.status, applicant.isPassed, applicant.isNotified)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun updateActionButtons(status: String, isPassed: Boolean?, isNotified: Boolean) {
        val buttonGroups = when {
            // 지원서 제출 완료 상태
            status == "submitted" -> listOf(
                listOf(
                    ApplicantActionButton(
                        text = "불합격",
                        actionType = "reject",
                        confirmMessage = "제이름을\n불합격 처리하시겠어요?\n이후에는 되돌릴 수 없습니다.",
                        isDangerous = true
                    ),
                    ApplicantActionButton(
                        text = "면접 제안",
                        actionType = "schedule_interview",
                        confirmMessage = "제이름에게\n면접을 제안하시겠어요?",
                        isDangerous = false
                    )
                )
            )

            // 면접 대기중 상태 (기존 interview_scheduled 대신)
            status == "interview_waiting" -> listOf(
                listOf(
                    ApplicantActionButton(
                        text = "면접 완료",
                        actionType = "complete_interview",
                        confirmMessage = "제이름의\n면접 완료 처리하시겠어요?",
                        isDangerous = false
                    )
                )
            )

            // 심사 대기중 상태 (기존 interview_completed 대신)
            status == "review_waiting" -> listOf(
                listOf(
                    ApplicantActionButton(
                        text = "불합격",
                        actionType = "reject",
                        confirmMessage = "제이름을\n불합격 처리하시겠어요?\n이후에는 되돌릴 수 없습니다.",
                        isDangerous = true
                    ),
                    ApplicantActionButton(
                        text = "최종 합격 처리",
                        actionType = "pass",
                        confirmMessage = "제이름을\n최종 합격 처리하시겠어요?",
                        isDangerous = false
                    )
                )
            )

            // 심사 완료 상태 - isPassed 값에 따라 다른 버튼
            status == "review_completed" -> {
                when {
                    isPassed == true && !isNotified -> {
                        // 합격했지만 아직 알림 안 보낸 경우
                        listOf(
                            listOf(
                                ApplicantActionButton(
                                    text = "합격 결과 공유",
                                    actionType = "notify_result",
                                    confirmMessage = "제이름에게\n합격 결과를 공유하시겠어요?",
                                    isDangerous = false
                                )
                            )
                        )
                    }
                    isPassed == false && !isNotified -> {
                        // 불합격했지만 아직 알림 안 보낸 경우
                        listOf(
                            listOf(
                                ApplicantActionButton(
                                    text = "불합격 결과 공유",
                                    actionType = "notify_rejection",
                                    confirmMessage = "제이름에게\n불합격 결과를 공유하시겠어요?",
                                    isDangerous = true
                                )
                            )
                        )
                    }
                    else -> {
                        // 이미 알림 완료된 경우 또는 미정인 경우
                        emptyList()
                    }
                }
            }

            else -> emptyList()
        }

        _actionButtons.value = buttonGroups
    }

    fun performAction(applicantId: String, actionType: String) {
        viewModelScope.launch {
            try {
                when (actionType) {
                    "schedule_interview" -> {
                        applicantRepository.scheduleInterviews(listOf(applicantId))
                    }
                    "complete_interview" -> {
                        applicantRepository.completeInterviews(listOf(applicantId))
                    }
                    "pass" -> {
                        applicantRepository.passApplicants(listOf(applicantId))
                    }
                    "reject" -> {
                        applicantRepository.failApplicants(listOf(applicantId))
                    }
                    "notify_result", "notify_rejection" -> {
                        applicantRepository.notifyResults(listOf(applicantId))
                    }
                }

                // 액션 완료 후 데이터 새로고침
                loadApplicantDetail(applicantId)

            } catch (e: Exception) {
                e.printStackTrace()
                // TODO: 에러 처리
            }
        }
    }

    fun sendMessage(applicantId: String) {
        viewModelScope.launch {
            try {
                // TODO: 쪽지 보내기 구현
                // 쪽지 화면으로 이동하거나 쪽지 작성 다이얼로그 표시
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}