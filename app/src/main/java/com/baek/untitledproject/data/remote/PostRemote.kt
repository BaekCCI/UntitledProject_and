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
import com.baek.untitledproject.common.utils.toTimestamp
import com.baek.untitledproject.data.model.CustomQuestionResponse
import com.baek.untitledproject.data.model.InterviewSlotResponse
import com.baek.untitledproject.data.model.PostResponse
import com.baek.untitledproject.data.model.mapper.separate
import com.baek.untitledproject.data.model.mapper.toDomain
import com.baek.untitledproject.data.model.mapper.toPostRead
import com.baek.untitledproject.data.model.mapper.toResponse
import com.baek.untitledproject.domain.data.Post
import com.baek.untitledproject.domain.data.PostRead
import com.baek.untitledproject.domain.data.PostWrite
import com.baek.untitledproject.domain.data.User
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.storage.storage

object PostRemote {
    //게시글 리스트 가져오기
    suspend fun getPostSummaryList(): List<PostSummary> {
        val db = FirebaseFirestore.getInstance()

        //posts 조회
        val postsSnap = db.collection("posts")
            .whereEqualTo("status", "recruiting")
            .get()
            .await()
        val postDocs = postsSnap.documents.sortedByDescending { it.getTimestamp("created_at") }
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
    suspend fun uploadPost(post: PostWrite, user: User): String = coroutineScope {
        val now = Timestamp.now()

        val db = FirebaseFirestore.getInstance()
        val storage = Firebase.storage

        val postRef = db.collection("posts").document()
        val postId = postRef.id

        val uploadedPaths = mutableListOf<String>()

        val imgDocs: List<Pair<DocumentReference, Map<String, Any>>> = try {
            post.imageUris.mapIndexed { index, uri ->
                async {
                    val imgRef = db.collection("post_images").document()
                    val imgId = imgRef.id

                    val path = "post_images/$postId/$imgId.jpg"
                    val storageRef = storage.reference.child(path)

                    storageRef.putFile(uri).await()
                    uploadedPaths += path
                    val imgUrl = storageRef.downloadUrl.await().toString()
                    imgRef to mapOf(
                        "image_id" to imgId,
                        "post_id" to postId,
                        "image_url" to imgUrl,
                        "image_order" to index.toLong(),
                        "created_at" to now
                    )
                }
            }.awaitAll()
        } catch (e: Exception) {
            uploadedPaths.forEach { p ->
                runCatching {
                    storage.reference.child(p).delete().await()
                }
            }
            throw e
        }

        //interview slot 데이터 생성
        val slotDocs = post.interviewSlot.flatMap { (date, times) ->
            val formattedDate = date.toTimestamp()
            times.flatMap { timeslot ->
                timeslot.separate(post.interviewSlotStep).map { time ->
                    val slotRef = db.collection("interview_slots").document()
                    val slotId = slotRef.id
                    slotRef to mapOf(
                        "slot_id" to slotId,
                        "post_id" to postId,
                        "interview_date" to formattedDate,
                        "interview_time" to time,
                        "max_capacity" to post.maxCapacity,
                        "current_reservations" to 0,
                        "created_at" to now
                    )
                }
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

        try {
            val writes = buildList {
                add(postRef to post.toResponse(postId, user, now))
                addAll(imgDocs)
                addAll(slotDocs)
                addAll(questionDocs)
            }

            //배치 한도 500 제한 -> chunk 처리
            writes.chunked(450).forEach { chunk ->
                db.runBatch { b ->
                    chunk.forEach { (ref, data) -> b.set(ref, data) }
                }.await()
            }
        } catch (e: Exception) {
            // Firestore 실패 시 업로드 롤백
            uploadedPaths.forEach { p ->
                runCatching { storage.reference.child(p).delete().await() }
            }
            throw e
        }

        postId
    }

    suspend fun getPostForRead(postId: String): PostRead = coroutineScope {
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


        //----- 조회 결과 -----
        val imageUris = imagesDef.await()
        val interviewSlot = slotsDef.await()

        postResponse.toPostRead(imageUris, interviewSlot)
    }

    //TODO: 수정 시 사용, 로직 변경 필요
    suspend fun getPostForEdit(postId: String): PostWrite = coroutineScope {
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


        //----- 조회 결과 -----
        val imageUris = imagesDef.await()
        val interviewSlot = slotsDef.await()

        postResponse.toPostRead(imageUris, interviewSlot)
        PostWrite()
    }


}