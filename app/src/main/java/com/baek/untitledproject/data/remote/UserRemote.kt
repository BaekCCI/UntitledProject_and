package com.baek.untitledproject.data.remote

import com.baek.untitledproject.common.utils.toLocalDate
import com.baek.untitledproject.data.model.UserResponse
import com.baek.untitledproject.domain.data.Block
import com.baek.untitledproject.domain.data.User
import com.google.firebase.Timestamp
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

//Users document 관리
class UserRemote @Inject constructor(
    private val db: FirebaseFirestore
) {
    private val users = db.collection("users")

    suspend fun userExist(userId: String): Boolean {
        return users.document(userId).get().await().exists()
    }

    suspend fun getUser(userId: String): UserResponse {
        val doc = users.document(userId).get().await()

        require(doc.exists()) { "존재하지 않는 유저입니다." }
        //Firestore 스냅샷 -> 서버 모델 (UserResponse)
        val userResponse = requireNotNull(doc.toObject(UserResponse::class.java)) {
            "UserResponse 매핑 실패"
        }
        return userResponse
    }

    suspend fun saveUser(user: UserResponse): UserResponse {

        val now = Timestamp.now()

        val userRef = users.document(user.user_id)

        val data = user.copy(
            created_at = user.created_at ?: now,
            updated_at = now
        )

        userRef.set(data).await()
        return requireNotNull(
            userRef.get().await().toObject(UserResponse::class.java)
        ) { "UserResponse 매핑 실패" }
    }

    suspend fun deleteUser(userId: String) {
        users.document(userId).delete().await()
    }

    suspend fun getBlockedUser(userId: String): List<Block> = coroutineScope {

        val snap = users.document(userId)
            .collection("blocks")
            .get().await()

        val docs = snap.documents.sortedByDescending { it.getTimestamp("created_at") }

        docs.mapNotNull { doc ->
            val blockId = doc.id
            val blockedUserId = doc.getString("blocked_user_id") ?: ""
            val createdAt = doc.getTimestamp("created_at")?.toLocalDate()

            if (blockedUserId.isBlank()) return@mapNotNull null

            async {
                val userSnap = users.document(blockedUserId).get().await()

                val blockedUserName = userSnap.getString("name") ?: ""
                Block(blockId, blockedUserId, blockedUserName, createdAt)
            }
        }.map { it.await() }
    }

    suspend fun unBlockUser(userId: String, blockId: String) {
        users.document(userId)
            .collection("blocks")
            .document(blockId)
            .delete().await()
    }

}