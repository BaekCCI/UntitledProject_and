package com.baek.untitledproject.data.model

import com.google.firebase.Timestamp


//서버 데이터 모델
data class PostResponse(
    val post_id: String = "",
    val author_user_id: String = "",
    val title: String = "",
    val organization: String = "",
    val content: String = "",
    val recruitment_start: Timestamp? = null,
    val recruitment_end: Timestamp? = null,
    val has_interview: Boolean = false,
    val interview_location: String? = null,
    val status: String = "",

    val requires_name: Boolean = false,
    val requires_student_id: Boolean = false,
    val requires_department: Boolean = false,
    val requires_gender: Boolean = false,
    val requires_age: Boolean = false,
    //val requires_phone: Boolean = false,

    val author_name: String = "",
    val author_organization: String = "",

    val created_at: Timestamp? = null,
    val updated_at: Timestamp? = null
)
