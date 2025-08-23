package com.baek.untitledproject.domain.data

data class User(
    val userId: String? = null,
    val email: String,
    val name: String,
    val birthYear: Int,
    val gender: String,
    val department: String,
    val studentId: String,
    val termsAgreed: Boolean = false,
    val privacyAgreed: Boolean = false
)