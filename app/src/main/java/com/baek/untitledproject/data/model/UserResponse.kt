package com.baek.untitledproject.data.model

import com.google.firebase.Timestamp

data class UserResponse(
    val user_id: String = "",
    val email: String = "",
    val name: String = "",
    val birth_year: Int = 0,
    val gender: String = "",
    val department: String = "",
    val student_id: String = "",
    val email_verified: Boolean = false,
    val terms_agreed: Boolean = false,
    val privacy_agreed: Boolean = false,
    val created_at: Timestamp? = null,
    val updated_at: Timestamp? = null
)