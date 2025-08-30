package com.baek.untitledproject.domain.repository

import android.net.Uri
import com.baek.untitledproject.data.local.model.EmailLinkResult
import com.baek.untitledproject.domain.utils.Result
import kotlinx.coroutines.flow.Flow

interface EmailVerifyRepository {
    suspend fun sendSignInLink(email: String): Result<Unit>
    suspend fun handleDeepLink(inputEmail: String?, uri: Uri): Result<EmailLinkResult>
    suspend fun getEmail(): String?
    val emailFlow:Flow<String?>
}