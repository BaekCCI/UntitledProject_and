package com.baek.untitledproject.data.remote

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

//Firebase Authentication 관리
class AuthRemote @Inject constructor(
    private val auth: FirebaseAuth
) {

    suspend fun login(email: String, password: String): String {
        val res = auth.signInWithEmailAndPassword(email, password).await()
        return res.user?.uid ?: error("로그인 실패, userId 없음")

    }

    //비밀번호 설정
    suspend fun setPassword(password: String) {
        val user = auth.currentUser ?: error("로그인 필요")

        try {
            user.updatePassword(password).await()
        } catch (e: Exception) {
            // password provider가 안 붙어있다면 링크
            val hasPasswordProvider = user.providerData.any { it.providerId == "password" }
            if (!hasPasswordProvider) {
                val email = user.email ?: error("이메일 정보가 없습니다.")
                val cred = EmailAuthProvider.getCredential(email, password)
                user.linkWithCredential(cred).await()
            } else {
                throw e
            }
        }
    }

    suspend fun logout() {
        auth.signOut()
    }

    suspend fun reauthenticate(email: String, password: String) {
        val u = auth.currentUser ?: error("로그인 필요")
        val cred = EmailAuthProvider.getCredential(email, password)
        u.reauthenticate(cred).await()
    }

    suspend fun deleteAuthAccount() {
        val user = auth.currentUser ?: error("로그인 필요")
        user.delete().await()
    }


}