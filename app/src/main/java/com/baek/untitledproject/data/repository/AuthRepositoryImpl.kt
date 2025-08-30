package com.baek.untitledproject.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.baek.untitledproject.data.local.EmailStore
import com.baek.untitledproject.data.local.model.EmailLinkResult
import com.baek.untitledproject.data.remote.AuthRemote
import com.baek.untitledproject.domain.repository.AuthRepository
import com.baek.untitledproject.domain.utils.Result
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class AuthRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val emailStore: EmailStore
) : AuthRepository {
    override suspend fun sendSignInLink(email: String): Result<Unit> {
        return try {
            emailStore.clear()
            AuthRemote.sendSignInLink(email, context.packageName)
            emailStore.saveEmail(email)
            Result.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("EmailVerifyRepository", "이메일 링크 전송 실패", e)
            Result.Error("인증 메일 전송에 실패했어요. 잠시 후 다시 시도해주세요.", e)

        }
    }

    override suspend fun handleDeepLink(inputEmail: String?, uri: Uri): Result<EmailLinkResult> {
        return try {
            val email = emailStore.getEmail() ?: inputEmail
            if (email == null) {
                return Result.Error("저장된 이메일이 없습니다. 이메일을 다시 입력해주세요.")
            }
            val result = AuthRemote.handleEmailSignInLink(uri, email)
            Result.Success(result)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("EmailVerifyRepository", "이메일 링크 로그인 실패", e)
            Result.Error("이메일 인증에 실패하였습니다.", e)
        }
    }

    override suspend fun getEmail(): String? {
        return emailStore.getEmail()
    }

    override val emailFlow: Flow<String?> =
        emailStore.flow

}