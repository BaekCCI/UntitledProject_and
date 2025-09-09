package com.baek.untitledproject.data.repository

import android.util.Log
import com.baek.untitledproject.data.remote.InterviewRemote
import com.baek.untitledproject.data.remote.MyRecruitsRemote
import com.baek.untitledproject.domain.data.AppliedRecruitSummary
import com.baek.untitledproject.domain.data.InterviewSlot
import com.baek.untitledproject.domain.data.MyRecruitSummary
import com.baek.untitledproject.domain.data.ScheduleGroupSummary
import com.baek.untitledproject.domain.repository.MyRecruitsRepository
import com.baek.untitledproject.domain.repository.SessionRepository
import javax.inject.Inject

class MyRecruitsRepositoryImpl @Inject constructor(
    private val sessionRepository: SessionRepository
) : MyRecruitsRepository {

    override suspend fun getMyRecruits(): List<MyRecruitSummary> {
        return try {
            val currentUserId = sessionRepository.currentUid()
                ?: throw IllegalStateException("로그인이 필요합니다")

            MyRecruitsRemote.getMyRecruits(currentUserId)
        } catch (e: Exception) {
            Log.e("MyRecruitsRepository", "내 공고 목록 조회 실패", e)
            emptyList()
        }
    }

    override suspend fun getAppliedRecruits(): List<AppliedRecruitSummary> {
        return try {
            val currentUserId = sessionRepository.currentUid()
                ?: throw IllegalStateException("로그인이 필요합니다")

            MyRecruitsRemote.getAppliedRecruits(currentUserId)
        } catch (e: Exception) {
            Log.e("MyRecruitsRepository", "지원한 공고 목록 조회 실패", e)
            emptyList()
        }
    }

    override suspend fun getScheduleGroups(): List<ScheduleGroupSummary> {
        return try {
            val currentUserId = sessionRepository.currentUid()
            if (currentUserId == null) {
                Log.w("MyRecruitsRepository", "로그인되지 않음 - 빈 리스트 반환")
                return emptyList()
            }

            MyRecruitsRemote.getScheduleGroups(currentUserId) // userId 전달
        } catch (e: Exception) {
            Log.e("MyRecruitsRepository", "면접 일정 조회 실패", e)
            emptyList()
        }
    }

    override suspend fun getAvailableInterviewSlots(postId: String): List<InterviewSlot> {
        return try {
            InterviewRemote.getAvailableInterviewSlots(postId)
        } catch (e: Exception) {
            Log.e("MyRecruitsRepository", "면접 시간대 조회 실패: postId=$postId", e)
            emptyList()
        }
    }

    override suspend fun reserveInterviewSlot(applicationId: String, slotId: String): Boolean {
        return try {
            InterviewRemote.reserveInterviewSlot(applicationId, slotId)
        } catch (e: Exception) {
            Log.e("MyRecruitsRepository", "면접 예약 실패: applicationId=$applicationId, slotId=$slotId", e)
            throw e
        }
    }

    override suspend fun cancelInterviewReservation(applicationId: String): Boolean {
        return try {
            InterviewRemote.cancelInterviewReservation(applicationId)
        } catch (e: Exception) {
            Log.e("MyRecruitsRepository", "면접 예약 취소 실패: applicationId=$applicationId", e)
            throw e
        }
    }
}