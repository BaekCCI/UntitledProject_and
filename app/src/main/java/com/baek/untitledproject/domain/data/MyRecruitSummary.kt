package com.baek.untitledproject.domain.data

data class MyRecruitSummary(
    val id: String,
    val category: String,        // "영상동아리"
    val title: String,           // "영상동아리 OO 모집합니다."
    val recruitStatus: String,   // "모집중", "모집완료"
    val thumbnailUrl: String?,   // 썸네일 이미지
    val imageUrls: List<String> = emptyList(), // 전체 이미지들
    val currentImageIndex: Int = 0,  // 현재 이미지 인덱스
    val applicantCount: Int = 0,     // 지원자 수
    val interviewCount: Int = 0      // 면접 예약 수
)