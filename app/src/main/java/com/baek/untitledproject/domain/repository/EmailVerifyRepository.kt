package com.baek.untitledproject.domain.repository

import android.net.Uri
import com.baek.untitledproject.data.local.model.AuthCache
import com.baek.untitledproject.domain.utils.Result
import com.baek.untitledproject.ui.login.AuthEntry

interface EmailVerifyRepository {

    suspend fun sendSignInLink(email: String, entry: AuthEntry): Result<Unit>
    suspend fun handleDeepLink(uri: Uri): Result<Boolean> // true -> 신규
    suspend fun saveAuthCache(email: String, entry: AuthEntry): Result<Unit>
    suspend fun getAuthCache(): Result<AuthCache>
    suspend fun clearAuthCache(): Result<Unit>
}