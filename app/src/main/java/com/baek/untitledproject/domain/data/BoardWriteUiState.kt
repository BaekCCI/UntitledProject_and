package com.baek.untitledproject.domain.data

import android.net.Uri

data class BoardWriteUiState(
    //1번째 페이지
    val title: String = "", //공고 제목
    val organization: String = "", //단체명/동아리명
    val content: String = "", //모집 내용 상세 설명
    val recruitStart: String = "", //모집 시작일 yy/MM/dd
    val recruitEnd: String = "", //모집 마감일 yy/MM/dd
    val imageUris: List<Uri> = emptyList(), //업로드한 이미지

    //2번째 페이지
    val hasInterview: Boolean = false, //면접 진행 여부
    val interviewSlot: Map<String, String>,// 인터뷰 시간대 <yy/MM/dd, HH:mm>
    val interviewLocation: String = "",//면접 장소

    //3번째 페이지
    val requiresName: Boolean = true,
    val requiresStudentId: Boolean = true,
    val requiresDepartment: Boolean = false,
    val requiresGender: Boolean = false,
    val requiresAge: Boolean = false,
    val requiresPhone: Boolean = true,

    val customQuestions : List<String> = emptyList() //커스텀 질문들
)

