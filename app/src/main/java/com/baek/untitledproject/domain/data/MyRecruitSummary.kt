package com.baek.untitledproject.domain.data

data class MyRecruitSummary(
    val id: String,                          // post_id
    val title: String,                       // title
    val category: String,                    // organization (단체명을 카테고리로 사용)
    val recruitStatus: String,               // status ("recruiting" -> "모집중", "completed" -> "모집완료")
    val thumbnailUrl: String? = null,        // 첫 번째 이미지 (post_images에서 조회)

    // 관리용 정보
    val hasInterview: Boolean,               // has_interview
    val applicantCount: Int = 0,             // 지원자 수 (applications 컬렉션에서 계산)
    val recruitmentEnd: String? = null       // recruitment_end (포맷된 날짜)
)