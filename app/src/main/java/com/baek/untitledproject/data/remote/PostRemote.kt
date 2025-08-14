package com.baek.untitledproject.data.remote

import android.net.Uri
import android.util.Log
import com.baek.untitledproject.domain.data.PostSummary
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import androidx.core.net.toUri
import com.baek.untitledproject.common.utils.toLocalDate
import com.baek.untitledproject.data.model.PostImageResponse
import com.baek.untitledproject.data.model.PostResponse
import com.baek.untitledproject.data.model.mapper.toDomain
import com.baek.untitledproject.domain.data.Post
import java.time.LocalDate

object PostRemote {
    //게시글 리스트 가져오기
    suspend fun getPostSummaryList(): List<PostSummary> {
        val db = FirebaseFirestore.getInstance()

        val postsSnap = db.collection("posts").get().await()
        val postDocs = postsSnap.documents
        val postIds = postDocs.map { it.id }
        val thumbMap = fetchThumbnailByPostId(db, postIds)

        return postDocs.map { doc ->
            PostSummary(
                postId = doc.id,
                organization = doc.getString("organization") ?: "",
                title = doc.getString("title") ?: "",
                status = doc.getString("status") ?: "",
                imgUri = thumbMap[doc.id]
            )
        }
    }

    suspend fun getPostById(postId: String):Post = coroutineScope {
        val db = FirebaseFirestore.getInstance()

        val postDoc = db.collection("posts")
            .document(postId)
            .get()
            .await()

        require(postDoc.exists()) { "존재하지 않는 게시글입니다." }
        val postResponse = requireNotNull(postDoc.toObject(PostResponse::class.java)) {
            "PostResponse 매핑 실패"
        }

        val imagesDef = async {
            db.collection("post_images")
                .whereEqualTo("post_id", postId)
                .get().await()
                .sortedBy { it.getLong("image_order") ?: 0L }
                .mapNotNull { it.getString("image_url")?.toUri() }
        }

        val slotsDef = async {
            db.collection("interview_slots")
                .whereEqualTo("post_id", postId)
                .get().await()
                .documents
                .mapNotNull { doc ->
                    val date = doc.getTimestamp("interview_date")?.toLocalDate()
                    val time = doc.getString("interview_time")
                    if (date != null && time != null) date to time else null
                }
                .sortedWith(compareBy({ it.first }, { it.second })) // "HH:mm" 가정
                .groupBy({ it.first }, { it.second })
                .mapValues { (_, times) -> times.distinct().sorted() }
                .toSortedMap()
        }

        val questionsDef = async {
            db.collection("custom_questions")
                .whereEqualTo("post_id", postId)
                .get().await()
                .documents
                .sortedBy { it.getLong("order") ?: 0L }
                .mapNotNull { it.getString("question_text") }
        }

        val imageUris = imagesDef.await()
        val interviewSlot = slotsDef.await()
        val customQuestions = questionsDef.await()

        postResponse.toDomain(imageUris, interviewSlot, customQuestions)
    }


    //썸네일 이미지 가져오기
    private suspend fun fetchThumbnailByPostId(
        db: FirebaseFirestore,
        postIds: List<String>
    ): Map<String, Uri> = coroutineScope {
        val chunks = postIds.chunked(10) // whereIn 최대 10개 제한
        val jobs = chunks.map { chunk ->
            async {
                db.collection("post_images")
                    .whereIn("post_id", chunk)
                    .whereEqualTo("image_order", 0)
                    .get()
                    .await()
                    .documents
                    .mapNotNull { d ->
                        val pId = d.getString("post_id")
                        val url = d.getString("image_url")
                        if (pId != null && url != null) pId to url.toUri() else null
                    }
            }
        }
        jobs.awaitAll().flatten().toMap()
    }
}