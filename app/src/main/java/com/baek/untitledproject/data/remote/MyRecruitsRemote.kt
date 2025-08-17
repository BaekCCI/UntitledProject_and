package com.baek.untitledproject.data.remote

import android.util.Log
import com.baek.untitledproject.domain.data.AppliedRecruitSummary
import com.baek.untitledproject.domain.data.MyRecruitSummary
import com.baek.untitledproject.domain.data.ScheduleGroupSummary
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

object MyRecruitsRemote {

    private const val myRecruitUserId = "test-author-123"
    private const val myAppliedUserId = "user2"

    suspend fun getMyRecruits(): List<MyRecruitSummary> {
        val db = FirebaseFirestore.getInstance()

        val postsSnap = db.collection("posts")
            .whereEqualTo("author_user_id", myRecruitUserId)
            .get()
            .await()

        return postsSnap.documents.mapNotNull { doc ->
            try {
                val data = doc.data ?: return@mapNotNull null

                MyRecruitSummary(
                    id = doc.id,
                    title = data["title"] as? String ?: "제목 없음",
                    category = data["organization"] as? String ?: "단체명 없음",
                    recruitStatus = getRecruitStatusText(data["status"] as? String),
                    thumbnailUrl = null,
                    hasInterview = data["has_interview"] as? Boolean ?: false,
                    applicantCount = 0,
                    recruitmentEnd = formatRecruitmentEnd(data["recruitment_end"])
                )
            } catch (e: Exception) {
                Log.e("MyRecruitsRemote", "내 공고 데이터 변환 실패: ${doc.id}", e)
                null
            }
        }
    }

    suspend fun getAppliedRecruits(): List<AppliedRecruitSummary> {
        val db = FirebaseFirestore.getInstance()

        val applicationsSnap = db.collection("applications")
            .whereEqualTo("applicant_user_id", myAppliedUserId)
            .get()
            .await()

        return applicationsSnap.documents.mapNotNull { doc ->
            try {
                val data = doc.data ?: return@mapNotNull null
                val postId = data["post_id"] as? String ?: return@mapNotNull null
                val postTitle = data["post_title"] as? String ?: "공고 제목"
                val postOrganization = data["post_organization"] as? String ?: "단체명"

                AppliedRecruitSummary(
                    id = doc.id,
                    postId = postId,
                    title = postTitle,
                    category = postOrganization,
                    recruitStatus = "모집중",
                    applicationStatus = getApplicationStatusText(
                        data["status"] as? String,
                        data["is_passed"] as? Boolean
                    ),
                    hasInterview = false,
                    interviewSlotId = data["interview_slot_id"] as? String,
                    canReserveInterview = canReserveInterview(
                        data["status"] as? String,
                        data["interview_slot_id"] as? String
                    )
                )
            } catch (e: Exception) {
                Log.e("MyRecruitsRemote", "지원한 공고 데이터 변환 실패: ${doc.id}", e)
                null
            }
        }
    }

    suspend fun getScheduleGroups(): List<ScheduleGroupSummary> {
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

    private fun formatRecruitmentEnd(recruitmentEnd: Any?): String? {
        return try {
            when (recruitmentEnd) {
                is com.google.firebase.Timestamp -> {
                    SimpleDateFormat("MM월 dd일", Locale.getDefault()).format(recruitmentEnd.toDate())
                }
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }
}