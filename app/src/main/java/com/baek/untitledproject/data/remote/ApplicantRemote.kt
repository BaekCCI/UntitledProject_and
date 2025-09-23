package com.baek.untitledproject.data.remote

import android.util.Log
import com.baek.untitledproject.domain.data.ApplicantSummary
import com.baek.untitledproject.domain.data.QuestionAnswer
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object ApplicantRemote {

    suspend fun getApplicants(postId: String, currentUserId: String): List<ApplicantSummary> {
        val db = FirebaseFirestore.getInstance()

        Log.d("ApplicantRemote", "currentUserId: $currentUserId")

        val applicationsSnap = db.collection("applications")
            .whereEqualTo("post_id", postId)
            .whereEqualTo("post_author_user_id", currentUserId)
            .get()
            .await()

        Log.d("ApplicantRemote", "조회 결과: ${applicationsSnap.size()}개")

        return applicationsSnap.documents.mapNotNull { doc ->
            try {
                val data = doc.data ?: return@mapNotNull null

                Log.d("ApplicantRemote", "문서 처리 중: ${doc.id}")
                Log.d("ApplicantRemote", "applicant_name: ${data["applicant_name"]}")

                val applicant = ApplicantSummary(
                    id = doc.id,
                    name = data["applicant_name"] as? String ?: "이름 없음",
                    gender = data["applicant_gender"] as? String ?: "미정",
                    age = calculateAge(data["applicant_birth_year"] as? Long),
                    department = data["applicant_department"] as? String,
                    studentId = data["applicant_student_id"] as? String,
                    phoneNumber = data["applicant_phone"] as? String,
                    status = data["status"] as? String ?: "submitted",
                    isPassed = data["is_passed"] as? Boolean,
                    isNotified = data["is_notified"] as? Boolean ?: false,
                    statusText = getStatusText(
                        data["status"] as? String ?: "submitted",
                        data["is_passed"] as? Boolean
                    ),
                    customQuestionAnswers = emptyList()
                )

                Log.d("ApplicantRemote", "변환 성공: ${applicant.name}, 상태: ${applicant.statusText}")
                applicant

            } catch (e: Exception) {
                Log.e("ApplicantRemote", "변환 실패: ${doc.id}", e)
                null
            }
        }.also { list ->
            Log.d("ApplicantRemote", "최종 반환 개수: ${list.size}개")
            list.forEach { applicant ->
                Log.d("ApplicantRemote", "반환 데이터: ${applicant.name}")
            }
        }
    }

    suspend fun getApplicantDetail(applicationId: String): ApplicantSummary {
        val db = FirebaseFirestore.getInstance()

        // 1. 기본 지원서 정보 조회
        val doc = db.collection("applications")
            .document(applicationId)
            .get()
            .await()

        require(doc.exists()) { "지원서를 찾을 수 없습니다: $applicationId" }
        val data = doc.data!!

        // 2. 커스텀 질문/답변 조회 (question_answers 컬렉션에서)
        val customQuestionAnswers = try {
            Log.d("ApplicantRemote", "커스텀 질문 조회 시작: $applicationId")

            db.collection("question_answers")
                .whereEqualTo("application_id", applicationId)
                .get()
                .await()
                .documents.map { answerDoc ->
                    val answerData = answerDoc.data ?: return@map null
                    QuestionAnswer(
                        answerId = answerDoc.id,  // Document ID
                        applicationId = answerData["application_id"] as? String ?: "",
                        questionId = answerData["question_id"] as? String ?: "",
                        questionText = answerData["question_text"] as? String ?: "",
                        answerText = answerData["answer_text"] as? String ?: "",
                        createdAt = answerData["created_at"]?.toString() ?: ""
                    )
                }.filterNotNull().also { answers ->
                    Log.d("ApplicantRemote", "커스텀 질문 조회 완료: ${answers.size}개")
                    answers.forEach { answer ->
                        Log.d("ApplicantRemote", "질문: ${answer.questionText}")
                        Log.d("ApplicantRemote", "답변: ${answer.answerText}")
                    }
                }
        } catch (e: Exception) {
            Log.e("ApplicantRemote", "커스텀 질문 조회 실패: $applicationId", e)
            emptyList()
        }

        return ApplicantSummary(
            id = doc.id,
            name = data["applicant_name"] as? String ?: "이름 없음",
            gender = data["applicant_gender"] as? String ?: "미정",
            age = calculateAge(data["applicant_birth_year"] as? Long),
            department = data["applicant_department"] as? String,
            studentId = data["applicant_student_id"] as? String,
            phoneNumber = data["applicant_phone"] as? String,
            status = data["status"] as? String ?: "submitted",
            isPassed = data["is_passed"] as? Boolean,
            isNotified = data["is_notified"] as? Boolean ?: false,
            statusText = getStatusText(
                data["status"] as? String ?: "submitted",
                data["is_passed"] as? Boolean
            ),
            customQuestionAnswers = customQuestionAnswers
        )
    }

    /**
     * 특정 공고에 면접 일정이 설정되어 있는지 확인
     */
    suspend fun hasInterviewSlots(postId: String): Boolean {
        val db = FirebaseFirestore.getInstance()

        return try {
            val slotsSnapshot = db.collection("interview_slots")
                .whereEqualTo("post_id", postId)
                .limit(1)  // 하나만 있어도 되므로 limit 설정
                .get()
                .await()

            val hasSlots = !slotsSnapshot.isEmpty
            Log.d("ApplicantRemote", "공고 $postId 면접 슬롯 확인: $hasSlots")

            hasSlots
        } catch (e: Exception) {
            Log.e("ApplicantRemote", "면접 슬롯 확인 실패: $postId", e)
            false
        }
    }

    /**
     * 지원자들을 면접 대기 상태로 변경 (면접 일정 체크 포함)
     */
    suspend fun scheduleInterviews(applicationIds: List<String>, currentUserId: String) {
        val db = FirebaseFirestore.getInstance()

        // 첫 번째 지원서를 통해 postId 확인
        if (applicationIds.isEmpty()) {
            throw IllegalArgumentException("지원자 ID 목록이 비어있습니다.")
        }

        val firstApplicationDoc = db.collection("applications")
            .document(applicationIds.first())
            .get()
            .await()

        val postId = firstApplicationDoc.getString("post_id")
            ?: throw IllegalStateException("공고 ID를 찾을 수 없습니다.")

        // 면접 일정 설정 확인
        val hasSlots = hasInterviewSlots(postId)
        if (!hasSlots) {
            throw IllegalStateException("면접 일정이 설정되지 않았습니다. 먼저 면접 일정을 설정해주세요.")
        }

        // 면접 일정이 있으면 상태 변경 진행
        applicationIds.forEach { applicationId ->
            db.collection("applications")
                .document(applicationId)
                .update(
                    mapOf(
                        "status" to "면접 대기 중",
                        "updated_at" to com.google.firebase.Timestamp.now()
                    )
                )
                .await()
        }
    }

    /**
     * 지원자들을 이전 단계로 되돌리기
     */
    suspend fun revertToPreviousStage(applicationIds: List<String>) {
        val db = FirebaseFirestore.getInstance()

        applicationIds.forEach { applicationId ->
            // 현재 상태 확인
            val applicationDoc = db.collection("applications")
                .document(applicationId)
                .get()
                .await()

            val currentStatus = applicationDoc.getString("status")

            // 이전 단계로 되돌리기
            val previousStatus = when (currentStatus) {
                "면접 대기 중" -> "지원서 제출됨"
                "심사 대기 중" -> "면접 대기 중"
                "심사 완료됨" -> "심사 대기 중"
                else -> {
                    Log.w("ApplicantRemote", "되돌릴 수 없는 상태: $currentStatus")
                    return@forEach // 이 지원자는 건너뛰기
                }
            }

            // 상태 업데이트
            val updateData = mutableMapOf<String, Any>(
                "status" to previousStatus,
                "updated_at" to com.google.firebase.Timestamp.now()
            )

            // 심사 완료됨에서 되돌릴 때는 is_passed 필드 삭제
            if (currentStatus == "심사 완료됨") {
                updateData["is_passed"] = com.google.firebase.firestore.FieldValue.delete()
            }

            db.collection("applications")
                .document(applicationId)
                .update(updateData)
                .await()

            Log.d("ApplicantRemote", "단계 되돌리기: $applicationId ($currentStatus → $previousStatus)")
        }
    }

    /**
     * 지원자들의 면접을 완료 상태로 변경
     */
    suspend fun completeInterviews(applicationIds: List<String>) {
        val db = FirebaseFirestore.getInstance()

        applicationIds.forEach { applicationId ->
            db.collection("applications")
                .document(applicationId)
                .update(
                    mapOf(
                        "status" to "심사 대기 중",
                        "updated_at" to com.google.firebase.Timestamp.now()
                    )
                )
                .await()
        }
    }

    /**
     * 지원자들을 합격 처리
     */
    suspend fun passApplicants(applicationIds: List<String>) {
        val db = FirebaseFirestore.getInstance()

        applicationIds.forEach { applicationId ->
            db.collection("applications")
                .document(applicationId)
                .update(
                    mapOf(
                        "status" to "심사 완료됨",
                        "is_passed" to true,
                        "updated_at" to com.google.firebase.Timestamp.now()
                    )
                )
                .await()
        }
    }

    /**
     * 지원자들을 불합격 처리
     */
    suspend fun failApplicants(applicationIds: List<String>) {
        val db = FirebaseFirestore.getInstance()

        applicationIds.forEach { applicationId ->
            db.collection("applications")
                .document(applicationId)
                .update(
                    mapOf(
                        "status" to "심사 완료됨",
                        "is_passed" to false,
                        "updated_at" to com.google.firebase.Timestamp.now()
                    )
                )
                .await()
        }
    }

    /**
     * 지원자들에게 결과 알림 발송
     */
    suspend fun notifyResults(applicationIds: List<String>, currentUserId: String) {
        val db = FirebaseFirestore.getInstance()

        applicationIds.forEach { applicationId ->
            // 1. notifications 컬렉션에 알림 추가
            val applicationDoc = db.collection("applications")
                .document(applicationId)
                .get()
                .await()

            val applicationData = applicationDoc.data!!
            val receiverUserId = applicationData["applicant_user_id"] as String
            val postTitle = applicationData["post_title"] as? String ?: "공고"
            val isPassed = applicationData["is_passed"] as? Boolean

            val notificationData = mapOf(
                "sender_user_id" to currentUserId,
                "receiver_user_id" to receiverUserId,
                "post_id" to applicationData["post_id"],
                "sender_organization" to applicationData["post_organization"],
                "title" to if (isPassed == true) "합격 알림" else "결과 알림",
                "message" to "${postTitle}에 대한 결과가 발표되었습니다.",
                "notification_type" to "result",
                "is_read" to false,
                "created_at" to com.google.firebase.Timestamp.now()
            )

            db.collection("notifications")
                .add(notificationData)
                .await()

            // 2. application에 알림 발송 완료 표시
            db.collection("applications")
                .document(applicationId)
                .update(
                    mapOf(
                        "is_notified" to true,
                        "updated_at" to com.google.firebase.Timestamp.now()
                    )
                )
                .await()
        }
    }

    // 유틸리티 함수들
    private fun calculateAge(birthYear: Long?): Int {
        return if (birthYear != null) {
            val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
            currentYear - birthYear.toInt() + 1
        } else {
            0
        }
    }

    private fun getStatusText(status: String, isPassed: Boolean?): String {
        return when {
            status == "지원서 제출됨" -> "지원서 제출 완료"  // 한글 상태값에 맞춤
            status == "면접 대기 중" -> "면접 대기중"
            status == "심사 대기 중" -> "심사 대기중"
            status == "심사 완료됨" && isPassed == true -> "합격"
            status == "심사 완료됨" && isPassed == false -> "불합격"
            status == "심사 완료됨" && isPassed == null -> "심사 완료"
            else -> "알 수 없음"
        }
    }
}