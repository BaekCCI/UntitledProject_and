package com.baek.untitledproject.data.remote

import com.baek.untitledproject.data.model.InterviewSlotResponse
import com.baek.untitledproject.domain.data.InterviewSlot
import com.baek.untitledproject.domain.data.toDomain
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object InterviewRemote {

    private val db = FirebaseFirestore.getInstance()

    /**
     * 해당 공고의 예약 가능한 면접 시간대 조회
     * current_reservations < max_capacity인 슬롯만 반환
     */
    suspend fun getAvailableInterviewSlots(postId: String): List<InterviewSlot> {
        return try {
            val querySnapshot = db.collection("interview_slots")
                .whereEqualTo("post_id", postId)
                .get()
                .await()

            querySnapshot.documents.mapNotNull { document ->
                val interviewDate = document.getTimestamp("interview_date")
                val interviewTime = document.getString("interview_time") ?: ""
                val maxCapacity = document.getLong("max_capacity")?.toInt() ?: 0
                val currentReservations = document.getLong("current_reservations")?.toInt() ?: 0

                val response = InterviewSlotResponse(
                    interview_date = interviewDate,
                    interview_time = interviewTime,
                    max_capacity = maxCapacity,
                    current_reservations = currentReservations
                )

                response.toDomain(document.id, postId)
            }.sortedWith(
                compareBy<InterviewSlot> { it.interviewDate }
                    .thenBy { it.interviewTime }
            )
        } catch (e: Exception) {
            throw Exception("면접 시간대 조회 실패: ${e.message}", e)
        }
    }

    /**
     * 면접 예약하기
     * 1. applications 문서 업데이트: interview_slot_id, interview_reservation_status 설정
     * 2. interview_slots 문서 업데이트: current_reservations 증가
     */
    suspend fun reserveInterviewSlot(applicationId: String, slotId: String): Boolean {
        return try {
            db.runTransaction { transaction ->
                // 1. 지원서 문서 조회
                val applicationRef = db.collection("applications").document(applicationId)
                val applicationSnapshot = transaction.get(applicationRef)

                if (!applicationSnapshot.exists()) {
                    throw Exception("존재하지 않는 지원서입니다.")
                }

                // 2. 면접 슬롯 문서 조회
                val slotRef = db.collection("interview_slots").document(slotId)
                val slotSnapshot = transaction.get(slotRef)

                if (!slotSnapshot.exists()) {
                    throw Exception("존재하지 않는 면접 시간대입니다.")
                }

                // 3. 예약 가능 여부 확인
                val currentReservations = slotSnapshot.getLong("current_reservations")?.toInt() ?: 0
                val maxCapacity = slotSnapshot.getLong("max_capacity")?.toInt() ?: 0

                if (currentReservations >= maxCapacity) {
                    throw Exception("해당 시간대는 이미 예약이 가득 찼습니다.")
                }

                // 4. 지원서에 이미 면접 예약이 있는지 확인
                val existingSlotId = applicationSnapshot.getString("interview_slot_id")
                if (!existingSlotId.isNullOrBlank()) {
                    throw Exception("이미 면접 예약이 되어 있습니다.")
                }

                // 5. 트랜잭션으로 두 문서 동시 업데이트
                // applications 문서 업데이트
                transaction.update(applicationRef, mapOf(
                    "interview_slot_id" to slotId,
                    "interview_reservation_status" to "reserved",
                    "status" to "interview_waiting"
                ))

                // interview_slots 문서 업데이트
                transaction.update(slotRef, mapOf(
                    "current_reservations" to (currentReservations + 1)
                ))

                true
            }.await()
        } catch (e: Exception) {
            throw Exception("면접 예약 실패: ${e.message}", e)
        }
    }

    /**
     * 면접 예약 취소
     * 1. applications 문서 업데이트: interview_slot_id null, interview_reservation_status null
     * 2. interview_slots 문서 업데이트: current_reservations 감소
     */
    suspend fun cancelInterviewReservation(applicationId: String): Boolean {
        return try {
            db.runTransaction { transaction ->
                // 1. 지원서 문서 조회
                val applicationRef = db.collection("applications").document(applicationId)
                val applicationSnapshot = transaction.get(applicationRef)

                if (!applicationSnapshot.exists()) {
                    throw Exception("존재하지 않는 지원서입니다.")
                }

                val slotId = applicationSnapshot.getString("interview_slot_id")
                if (slotId.isNullOrBlank()) {
                    throw Exception("예약된 면접이 없습니다.")
                }

                // 2. 면접 슬롯 문서 조회
                val slotRef = db.collection("interview_slots").document(slotId)
                val slotSnapshot = transaction.get(slotRef)

                if (!slotSnapshot.exists()) {
                    throw Exception("존재하지 않는 면접 시간대입니다.")
                }

                val currentReservations = slotSnapshot.getLong("current_reservations")?.toInt() ?: 0

                // 3. 트랜잭션으로 두 문서 동시 업데이트
                // applications 문서 업데이트
                transaction.update(applicationRef, mapOf(
                    "interview_slot_id" to null,
                    "interview_reservation_status" to null,
                    "status" to "submitted"
                ))

                // interview_slots 문서 업데이트
                transaction.update(slotRef, mapOf(
                    "current_reservations" to maxOf(0, currentReservations - 1)
                ))

                true
            }.await()
        } catch (e: Exception) {
            throw Exception("면접 예약 취소 실패: ${e.message}", e)
        }
    }
}