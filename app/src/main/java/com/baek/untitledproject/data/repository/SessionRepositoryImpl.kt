package com.baek.untitledproject.data.repository

import android.util.Log
import com.baek.untitledproject.domain.repository.SessionRepository
import com.baek.untitledproject.domain.utils.SessionState
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

class SessionRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth
) : SessionRepository {

    override val session: Flow<SessionState> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            Log.d("SessionRepository", "AuthState changed")
            trySend(if (user == null) SessionState.LoggedOut else SessionState.LoggedIn(user.uid))
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }.distinctUntilChanged()

    override fun currentUid(): String? {
        return auth.currentUser?.uid
    }

    override fun isLoggedIn(): Boolean {
        return auth.currentUser != null
    }
}
