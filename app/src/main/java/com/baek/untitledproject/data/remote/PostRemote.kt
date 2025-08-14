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

        //posts 조회
        val postsSnap = db.collection("posts").get().await()
        val postDocs = postsSnap.documents
        val postIds = postDocs.map { it.id }

        //썸네일 이미지 조회(postId to Uri)
        val thumbMap = fetchThumbnailByPostId(db, postIds)

        //도메인 모델로 매핑
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

    //게시글 상세 조회
    suspend fun getPostById(postId: String):Post = coroutineScope {
        val db = FirebaseFirestore.getInstance()

        //post 조회
        val postDoc = db.collection("posts")
            .document(postId)
            .get()
            .await()

        require(postDoc.exists()) { "존재하지 않는 게시글입니다." }
        //Firestore 스냅샷 -> 서버 모델 (PostResponse)
        val postResponse = requireNotNull(postDoc.toObject(PostResponse::class.java)) {
            "PostResponse 매핑 실패"
        }

        //----- 연관 데이터 병렬 조회 -----

        //이미지 조회: 서버 정렬 대신 직접 정렬로 우회(인덱스가 없어서 orderBy 안됨..)
        val imagesDef = async {
            db.collection("post_images")
                .whereEqualTo("post_id", postId)
                .get().await()
                .documents
                .sortedBy { it.getLong("image_order") ?: 0L }
                .mapNotNull { it.getString("image_url")?.toUri() }
        }

        //interview_slots 조회: 마찬가지로 직접 정렬
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

        //커스텀 질문 조회
        val questionsDef = async {
            db.collection("custom_questions")
                .whereEqualTo("post_id", postId)
                .get().await()
                .documents
                .sortedBy { it.getLong("order") ?: 0L }
                .mapNotNull { it.getString("question_text") }
        }

        //----- 조회 결과 -----
        val imageUris = imagesDef.await()
        val interviewSlot = slotsDef.await()
        val customQuestions = questionsDef.await()

        //서버 모델 -> 도메인 모델(Post) 매핑
        postResponse.toDomain(imageUris, interviewSlot, customQuestions)
    }


    //게시글 id 리스트에 대한 썸네일 이미지 가져오기
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