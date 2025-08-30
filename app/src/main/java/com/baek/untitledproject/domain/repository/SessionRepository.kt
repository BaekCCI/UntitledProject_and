package com.baek.untitledproject.domain.repository

import com.baek.untitledproject.domain.utils.SessionState
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    val session: Flow<SessionState>
    fun currentUid(): String?
    fun isLoggedIn(): Boolean
}