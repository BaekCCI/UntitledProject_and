package com.baek.untitledproject.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.baek.untitledproject.data.local.EmailStore
import com.baek.untitledproject.data.local.model.AuthCache
import com.baek.untitledproject.data.remote.AuthRemote
import com.baek.untitledproject.domain.repository.EmailVerifyRepository
import com.baek.untitledproject.domain.utils.Result
import com.baek.untitledproject.ui.login.AuthEntry
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

class EmailVerifyRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val emailStore: EmailStore
) : EmailVerifyRepository {

    override suspend fun sendSignInLink(email: String, entry: AuthEntry): Result<Unit> {
        return try {
            AuthRemote.sendSignInLink(email, context.packageName)
            emailStore.save(email, entry)
            Result.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("EmailVerifyRepository", "이메일 링크 전송 실패", e)
            Result.Error("인증 메일 전송에 실패했어요. 잠시 후 다시 시도해주세요.", e)

        }
    }

    override suspend fun handleDeepLink(uri: Uri): Result<Boolean> {
        return try {
            val email = emailStore.get().email ?: ""

            if (email.isBlank()) {
                return Result.Error("저장된 이메일이 없습니다. 이메일을 다시 입력해주세요.")
            }

            val isNew = AuthRemote.handleEmailSignInLink(uri, email)
            Result.Success(isNew)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("EmailVerifyRepository", "이메일 링크 로그인 실패", e)
            Result.Error("이메일 인증에 실패하였습니다.", e)
        }
    }

    override suspend fun saveAuthCache(email: String, entry: AuthEntry): Result<Unit> {
        return try {
            emailStore.save(email, entry)
            Result.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("EmailVerifyRepository", "이메일 저장 실패", e)
            Result.Error("이메일 저장에 실패했습니다.", e)
        }
    }

    override suspend fun getAuthCache(): Result<AuthCache> {
        return try {
            Result.Success(emailStore.get())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("EmailVerifyRepository", "이메일 불러오기 실패", e)
            Result.Error("저장된 이메일을 불러오지 못했습니다.", e)
        }
    }

    override suspend fun clearAuthCache(): Result<Unit> {
        return try {
            emailStore.clearAuth()
            Result.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("EmailVerifyRepository", "캐시 삭제 실패", e)
            Result.Error("캐시를 삭제하기 못했습니다.", e)
        }
    }
}