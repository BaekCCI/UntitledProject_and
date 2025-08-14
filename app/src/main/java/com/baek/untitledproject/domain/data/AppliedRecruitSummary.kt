package com.baek.untitledproject.domain.data

data class AppliedRecruitSummary(
    val id: String,                          // application_id
    val postId: String,                      // post_id
    val title: String,                       // post_title (비정규화)
    val category: String,                    // post_organization (비정규화)
    val recruitStatus: String,               // 공고 상태 (posts.status에서 조회)
    val applicationStatus: String,           // 지원 상태 (applications.status + is_passed 조합)

    // 면접 관련 정보
    val hasInterview: Boolean = false,       // 면접 있는 공고인지 (posts.has_interview에서 조회)
    val interviewSlotId: String? = null,     // interview_slot_id
    val canReserveInterview: Boolean = false // 면접 예약 가능 여부 (계산된 값)
)