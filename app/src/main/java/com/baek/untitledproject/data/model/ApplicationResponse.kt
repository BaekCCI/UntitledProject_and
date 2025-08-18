package com.baek.untitledproject.data.model

import com.google.firebase.Timestamp

data class ApplicationResponse(
    val application_id: String = "",
    val applicant_user_id: String = "",
    val post_id: String = "",
    val status: String = "",
    val is_passed: Boolean? = null,
    val interview_slot_id: String? = null,
    val interview_reservation_status: String? = null,

    // 지원자 정보
    val applicant_name: String = "",
    val applicant_birth_year: Int? = null,
    val applicant_gender: String? = null,
    val applicant_department: String? = null,
    val applicant_student_id: String? = null,
    val applicant_phone: String? = null,

    // 공고 정보
    val post_title: String = "",
    val post_organization: String = "",
    val post_author_user_id: String = "",

    val applied_at: Timestamp? = null,
    val updated_at: Timestamp? = null
)