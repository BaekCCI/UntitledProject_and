package com.baek.untitledproject.data.local.model

import com.baek.untitledproject.ui.login.AuthEntry

data class AuthCache(
    val email: String?,
    val entry: AuthEntry?,
    val savedAt: Long = 0L
)