package com.baek.untitledproject.domain.utils

sealed class SessionState {
    data object LoggedOut : SessionState()
    data class LoggedIn(val userId: String) : SessionState()
}