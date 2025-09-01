package com.baek.untitledproject.data.remote

import android.util.Log
import com.baek.untitledproject.data.model.NotificationResponse
import com.baek.untitledproject.data.model.PostResponse
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class NotificationRemote @Inject constructor(
    private val db: FirebaseFirestore
) {

    private val notifications = db.collection("notifications")


    fun hasNewNotification(userId: String): Flow<Boolean> = callbackFlow {
        val reg = notifications
            .whereEqualTo("receiver_user_id", userId)
            .whereEqualTo("is_read", false)
            .limit(1)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    trySend(false)
                    return@addSnapshotListener
                }
                trySend(value != null && !value.isEmpty)
            }
        awaitClose { reg.remove() }
    }

    suspend fun getNotifications(userId: String): List<NotificationResponse> {

        val snap = notifications.whereEqualTo("receiver_user_id", userId)
            .get().await()

        val docs = snap.documents.sortedByDescending { it.getTimestamp("created_at") }
        return docs.map { doc ->
            requireNotNull(doc.toObject(NotificationResponse::class.java)) {
                "NotificationResponse 매핑 실패: ${doc.id}"
            }
        }
    }

    suspend fun markAllAsRead(userId: String) {
        val snap = notifications
            .whereEqualTo("receiver_user_id", userId)
            .whereEqualTo("is_read", false)
            .get()
            .await()
        if (snap.isEmpty) return

        val batch = db.batch()
        snap.documents.forEach { doc ->
            batch.update(doc.reference, "is_read", true)
        }
        batch.commit().await()
    }
}