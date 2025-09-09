package com.baek.untitledproject.data.remote

import android.util.Log
import com.baek.untitledproject.domain.data.AppliedRecruitSummary
import com.baek.untitledproject.domain.data.MyRecruitSummary
import com.baek.untitledproject.domain.data.ScheduleGroupSummary
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldPath
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import com.baek.untitledproject.common.utils.toLocalDate

object MyRecruitsRemote {

    suspend fun getMyRecruits(currentUserId: String): List<MyRecruitSummary> {
        val db = FirebaseFirestore.getInstance()

        val postsSnap = db.collection("posts")
            .whereEqualTo("author_user_id", currentUserId)
            .get()
            .await()

        val postIds = postsSnap.documents.map { it.id }

        // 썸네일 맵
        val thumbMap = fetchThumbnailByPostId(db, postIds)

        return postsSnap.documents.mapNotNull { doc ->
            try {
                val title = doc.getString("title") ?: "제목 없음"
                val org = doc.getString("organization") ?: "단체명 없음"
                val status = getRecruitStatusText(doc.getString("status"))
                val hasInterview = doc.getBoolean("has_interview") ?: false

                // Firestore Timestamp -> LocalDate
                val start: LocalDate? = doc.getTimestamp("recruitment_start")?.toLocalDate()
                val end: LocalDate? = doc.getTimestamp("recruitment_end")?.toLocalDate()

                MyRecruitSummary(
                    id = doc.id,
                    title = title,
                    category = org,
                    recruitStatus = status,
                    thumbnailUrl = thumbMap[doc.id],
                    hasInterview = hasInterview,
                    applicantCount = 0,
                    recruitmentStart = start,
                    recruitmentEnd = end
                )
            } catch (e: Exception) {
                Log.e("MyRecruitsRemote", "내 공고 데이터 변환 실패: ${doc.id}", e)
                null
            }
        }
    }

    // 썸네일 조회 (post_images에서 order 0)
    private suspend fun fetchThumbnailByPostId(
        db: FirebaseFirestore,
        postIds: List<String>
    ): Map<String, String> = coroutineScope {
        val chunks = postIds.chunked(10) // whereIn 제한
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
                        if (pId != null && url != null) pId to url else null
                    }
            }
        }
        jobs.awaitAll().flatten().toMap()
    }

    suspend fun getAppliedRecruits(currentUserId: String): List<AppliedRecruitSummary> {
        val db = FirebaseFirestore.getInstance()

        val applicationsSnap = db.collection("applications")
            .whereEqualTo("applicant_user_id", currentUserId)
            .get()
            .await()

        // post_id 목록
        val postIds = applicationsSnap.documents.mapNotNull { it.getString("post_id") }.distinct()

        // 썸네일 맵
        val thumbMap = fetchThumbnailByPostId(db, postIds)

        // posts 메타(상태/모집기간) 맵
        val postMeta = fetchPostsMetaByIds(db, postIds)

        return applicationsSnap.documents.mapNotNull { doc ->
            try {
                val data = doc.data ?: return@mapNotNull null
                val postId = data["post_id"] as? String ?: return@mapNotNull null

                val title = data["post_title"] as? String ?: "공고 제목"
                val org = data["post_organization"] as? String ?: "단체명"

                val meta = postMeta[postId]
                val recruitStatus = getRecruitStatusText(meta?.status)

                AppliedRecruitSummary(
                    id = doc.id,
                    postId = postId,
                    title = title,
                    category = org,
                    recruitStatus = recruitStatus,
                    applicationStatus = getApplicationStatusText(
                        data["status"] as? String,
                        data["is_passed"] as? Boolean
                    ),
                    hasInterview = false,
                    interviewSlotId = data["interview_slot_id"] as? String,
                    canReserveInterview = canReserveInterview(
                        data["status"] as? String,
                        data["interview_slot_id"] as? String
                    ),
                    thumbnailUrl = thumbMap[postId],

                    recruitmentStart = meta?.recruitmentStart,
                    recruitmentEnd = meta?.recruitmentEnd
                )
            } catch (e: Exception) {
                Log.e("MyRecruitsRemote", "지원한 공고 데이터 변환 실패: ${doc.id}", e)
                null
            }
        }
    }

    suspend fun getScheduleGroups(currentUserId: String): List<ScheduleGroupSummary> {
        return emptyList()
    }

    private fun getRecruitStatusText(status: String?): String {
        return when (status) {
            "recruiting" -> "모집중"
            "completed" -> "모집완료"
            "closed" -> "마감"
            else -> "모집중"
        }
    }

    private fun getApplicationStatusText(status: String?, isPassed: Boolean?): String {
        return when {
            status == "지원서 제출됨" -> "지원 완료"
            status == "면접 대기 중" -> "면접 대기"
            status == "심사 대기 중" -> "심사중"
            status == "심사 완료됨" && isPassed == true -> "합격"
            status == "심사 완료됨" && isPassed == false -> "불합격"
            status == "심사 완료됨" && isPassed == null -> "결과 대기"
            else -> "지원 완료"
        }
    }

    private fun canReserveInterview(status: String?, interviewSlotId: String?): Boolean {
        return status == "면접 대기 중" && interviewSlotId == null
    }

    private suspend fun fetchPostsMetaByIds(
        db: FirebaseFirestore,
        postIds: List<String>
    ): Map<String, PostMeta> = coroutineScope {
        if (postIds.isEmpty()) return@coroutineScope emptyMap()
        val chunks = postIds.chunked(10)
        val jobs = chunks.map { chunk ->
            async {
                db.collection("posts")
                    .whereIn(FieldPath.documentId(), chunk)
                    .get()
                    .await()
                    .documents
                    .map { d ->
                        val status = d.getString("status")
                        val start: LocalDate? = d.getTimestamp("recruitment_start")?.toLocalDate()
                        val end: LocalDate? = d.getTimestamp("recruitment_end")?.toLocalDate()
                        d.id to PostMeta(status, start, end)
                    }
            }
        }
        jobs.awaitAll().flatten().toMap()
    }

    // 내부용 메타
    private data class PostMeta(
        val status: String?,
        val recruitmentStart: LocalDate?,
        val recruitmentEnd: LocalDate?
    )
}
