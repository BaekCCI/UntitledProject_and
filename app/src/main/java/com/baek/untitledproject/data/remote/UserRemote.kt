package com.baek.untitledproject.data.remote

import com.baek.untitledproject.data.model.UserResponse
import com.baek.untitledproject.domain.data.User
import com.google.firebase.Timestamp
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object UserRemote {

    suspend fun userExist(userId: String): Boolean {
        val db = FirebaseFirestore.getInstance()
        val doc = db.collection("users")
            .document(userId)
            .get()
            .await()
        return doc.exists()
    }

    suspend fun getUser(userId: String): UserResponse {
        val db = FirebaseFirestore.getInstance()

        val doc = db.collection("users")
            .document(userId)
            .get()
            .await()

        require(doc.exists()) { "존재하지 않는 유저입니다." }
        //Firestore 스냅샷 -> 서버 모델 (UserResponse)
        val userResponse = requireNotNull(doc.toObject(UserResponse::class.java)) {
            "UserResponse 매핑 실패"
        }
        return userResponse
    }

    //비밀번호 설정
    suspend fun setPassword(password: String) {

        val user = requireNotNull(FirebaseAuth.getInstance().currentUser)
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

    suspend fun saveUser(user: UserResponse): UserResponse {

        val now = Timestamp.now()
        val db = FirebaseFirestore.getInstance()

        val userRef = db.collection("users").document(user.user_id)

        val userWithTime = user.copy(created_at = now, updated_at = now)

        userRef.set(userWithTime).await()
        val snap = userRef.get().await()
        return requireNotNull(snap.toObject(UserResponse::class.java)) { "UserResponse 매핑 실패" }
    }

    suspend fun login(email: String, password: String): String {
        val result = FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(email, password).await()

        return result.user?.uid ?: throw IllegalStateException("로그인 실패, 유저 없음")

    }

    suspend fun deleteUser(userId: String) {
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("users").document(userId)
        userRef.delete().await()
    }

}