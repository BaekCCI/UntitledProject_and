package com.baek.untitledproject.data.repository

import android.util.Log
import com.baek.untitledproject.data.remote.AuthRemote
import com.baek.untitledproject.domain.utils.Result
import com.baek.untitledproject.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authRemote: AuthRemote

) : AuthRepository {
    override suspend fun login(email: String, password: String): Result<String> {
        return try {
            val userId = authRemote.login(email, password)
            Result.Success(userId)
        } catch (e: Exception) {
            Log.e("AuthRepository", "로그인 실패", e)
            Result.Error("로그인을 실패하였습니다.", e)
        }
    }

    override suspend fun setPassword(password: String): Result<Unit> {
        return try {
            authRemote.setPassword(password)
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e("AuthRepository", "비밀번호 저장 실패", e)
            Result.Error("비밀번호를 저장하는 데 실패하였습니다.", e)
        }
    }

    override suspend fun reAuthenticate(password: String): Result<String> {
        return try {
            val userId = authRemote.reAuthenticate(password)
            Result.Success(userId)
        } catch (e: Exception) {
            Log.e("AuthRepository", "재인증 실패", e)
            Result.Error("재인증에 실패했습니다. 이메일/비밀번호를 확인해주세요.", e)
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            authRemote.logout()
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e("AuthRepository", "로그아웃 실패", e)
            Result.Error("로그아웃에 실패하였습니다.", e)
        }
    }

    override suspend fun deleteAccount(): Result<Unit> {
        return try {
            authRemote.deleteAuthAccount()
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e("AuthRepository", "회원탈퇴 실패", e)
            Result.Error("회원탈퇴에 실패하였습니다.", e)
        }
    }
}