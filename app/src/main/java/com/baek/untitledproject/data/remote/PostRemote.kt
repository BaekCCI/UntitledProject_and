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
import com.baek.untitledproject.data.model.CustomQuestionResponse
import com.baek.untitledproject.data.model.InterviewSlotResponse
import com.baek.untitledproject.data.model.PostResponse
import com.baek.untitledproject.data.model.mapper.toDomain
import com.baek.untitledproject.data.model.mapper.toResponse
import com.baek.untitledproject.domain.data.Post
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.storage.storage

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
    suspend fun getPostById(postId: String): Post = coroutineScope {
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
                .mapNotNull { it.toObject(InterviewSlotResponse::class.java) }
        }

        //커스텀 질문 조회
        val questionsDef = async {
            db.collection("custom_questions")
                .whereEqualTo("post_id", postId)
                .get().await()
                .documents
                .mapNotNull { it.toObject(CustomQuestionResponse::class.java) }
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

    //-----공고 올리기-----

    suspend fun uploadPost(post: Post): String = coroutineScope {
        val now = Timestamp.now()

        val db = FirebaseFirestore.getInstance()
        val storage = Firebase.storage

        //posts 문서 생성
        val postRef = db.collection("posts").document()
        val postId = postRef.id


        //이미지 업로드
        val imageResult = post.imageUris.mapIndexed { index, uri ->
            async {
                //post_images 컬렉션 문서 생성
                val imgRef = db.collection("post_images").document()
                val imgId = imgRef.id


                //Storage에 저장할 경로 정의
                val path = "post_images/$postId/$imgId.jpg"
                val storageRef = storage.reference.child(path)

                //Storage 업로드
                storageRef.putFile(uri).await()
                //업로드 완료 후 다운로드 URL 획득
                val downloadUrl = storageRef.downloadUrl.await().toString()

                //Firestore에 넣을 데이터
                imgRef to mapOf(
                    "image_id" to imgId,
                    "post_id" to postId,
                    "image_url" to downloadUrl,
                    "image_order" to index.toLong(),
                    "created_at" to now
                )
            }
        }.awaitAll()// 모든 이미지 업로드가 완료될 때까지 대기

        //interview slot 데이터 생성
        val slotDocs = post.interviewSlot.flatMap { (date, times) ->
            times.map { time ->
                val slotRef = db.collection("interview_slots").document()
                val slotId = slotRef.id
                slotRef to mapOf(
                    "slot_id" to slotId,
                    "post_id" to postId,
                    "interview_date" to date,
                    "interview_time" to time,
                    "max_capacity" to post.maxCapacity,
                    "current_reservations" to 0,
                    "created_at" to now
                )
            }
        }

        //커스텀 질문 데이터
        val questionDocs = post.customQuestions.mapIndexed { index, q ->
            val questionRef = db.collection("custom_questions").document()
            val questionId = questionRef.id
            questionRef to mapOf(
                "question_id" to questionId,
                "post_id" to postId,
                "question_text" to q,
                "question_order" to index + 1,
                "created_at" to now
            )
        }

        //Firestore에 커밋
        db.runBatch { b ->
            b.set(postRef, post.toResponse(postId, now)) // 필요 시 serverTimestamp로 통일 가능
            imageResult.forEach { (ref, data) -> b.set(ref, data) }
            slotDocs.forEach { (ref, data) -> b.set(ref, data) }
            questionDocs.forEach { (ref, data) -> b.set(ref, data) }
        }.await()

        return@coroutineScope postId
    }
}