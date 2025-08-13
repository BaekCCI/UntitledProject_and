package com.baek.untitledproject.domain.data


import android.net.Uri
import java.time.LocalDate

data class Post(
    val postId: String? = "",
    val title: String? = null, //공고 제목
    val organization: String? = null, //단체명/동아리명
    val content: String? = null, //모집 내용 상세 설명
    val recruitmentStart: LocalDate? = null, //모집 시작일
    val recruitmentEnd: LocalDate? = null, //모집 마감일
    val imageUris: List<Uri> = emptyList(), //업로드한 이미지

    val hasInterview: Boolean? = null, //면접 진행 여부
    val interviewSlot: Map<LocalDate, String> = emptyMap(), // 인터뷰 시간대 <yy/MM/dd, HH:mm>
    val interviewStart : LocalDate?=null,
    val interviewEnd:LocalDate?=null,
    val interviewLocation: String? = null,//면접 장소

    val requiresName: Boolean = false,
    val requiresStudentId: Boolean = false,
    val requiresDepartment: Boolean = false,
    val requiresGender: Boolean = false,
    val requiresAge: Boolean = false,
    val requiresPhone: Boolean = false,

    val customQuestions: List<String> = emptyList() //커스텀 질문들
)

