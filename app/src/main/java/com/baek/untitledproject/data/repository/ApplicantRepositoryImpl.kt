package com.baek.untitledproject.data.repository

import android.util.Log
import com.baek.untitledproject.data.remote.ApplicantRemote
import com.baek.untitledproject.domain.data.ApplicantSummary
import com.baek.untitledproject.domain.repository.ApplicantRepository
import com.baek.untitledproject.domain.repository.SessionRepository
import com.baek.untitledproject.domain.utils.Result
import javax.inject.Inject

class ApplicantRepositoryImpl @Inject constructor(
    private val sessionRepository: SessionRepository
) : ApplicantRepository {

    override suspend fun getApplicants(postId: String): List<ApplicantSummary> {
        return try {
            val currentUserId = sessionRepository.currentUid()
                ?: throw IllegalStateException("로그인이 필요합니다")

            val result = ApplicantRemote.getApplicants(postId, currentUserId)
            Log.d("ApplicantRepository", "Repository에서 수신: ${result.size}개")
            result.forEach { applicant ->
                Log.d("ApplicantRepository", "Repository 데이터: ${applicant.name}")
            }
            result
        } catch (e: Exception) {
            Log.e("ApplicantRepository", "지원자 목록 조회 실패", e)
            emptyList()
        }
    }

    override suspend fun getApplicantDetail(applicationId: String): ApplicantSummary {
        return try {
            ApplicantRemote.getApplicantDetail(applicationId)
        } catch (e: Exception) {
            Log.e("ApplicantRepository", "지원자 상세 조회 실패", e)
            throw e
        }
    }

    override suspend fun hasInterviewSlots(postId: String): Result<Boolean> {
        return try {
            val hasSlots = ApplicantRemote.hasInterviewSlots(postId)
            Log.d("ApplicantRepository", "면접 슬롯 확인 완료: $postId - $hasSlots")
            Result.Success(hasSlots)
        } catch (e: Exception) {
            Log.e("ApplicantRepository", "면접 슬롯 확인 실패", e)
            Result.Error("면접 일정 확인에 실패했습니다.", e)
        }
    }

    override suspend fun scheduleInterviews(applicationIds: List<String>): Result<Unit> {
        return try {
            val currentUserId = sessionRepository.currentUid()
                ?: return Result.Error("로그인이 필요합니다.", IllegalStateException())

            ApplicantRemote.scheduleInterviews(applicationIds, currentUserId)
            Log.d("ApplicantRepository", "면접 일정 설정 완료: $applicationIds")
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e("ApplicantRepository", "면접 일정 설정 실패", e)
            val errorMessage = when {
                e.message?.contains("면접 일정이 설정되지 않았습니다") == true ->
                    "면접 일정이 설정되지 않았습니다. 먼저 면접 일정을 설정해주세요."
                else -> "면접 일정 설정에 실패했습니다."
            }
            Result.Error(errorMessage, e)
        }
    }

    override suspend fun completeInterviews(applicationIds: List<String>): Result<Unit> {
        return try {
            ApplicantRemote.completeInterviews(applicationIds)
            Log.d("ApplicantRepository", "면접 완료 처리 완료: $applicationIds")
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e("ApplicantRepository", "면접 완료 처리 실패", e)
            Result.Error("면접 완료 처리에 실패했습니다.", e)
        }
    }

    override suspend fun passApplicants(applicationIds: List<String>): Result<Unit> {
        return try {
            ApplicantRemote.passApplicants(applicationIds)
            Log.d("ApplicantRepository", "합격 처리 완료: $applicationIds")
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e("ApplicantRepository", "합격 처리 실패", e)
            Result.Error("합격 처리에 실패했습니다.", e)
        }
    }

    override suspend fun failApplicants(applicationIds: List<String>): Result<Unit> {
        return try {
            ApplicantRemote.failApplicants(applicationIds)
            Log.d("ApplicantRepository", "불합격 처리 완료: $applicationIds")
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e("ApplicantRepository", "불합격 처리 실패", e)
            Result.Error("불합격 처리에 실패했습니다.", e)
        }
    }

    override suspend fun notifyResults(applicationIds: List<String>): Result<Unit> {
        return try {
            val currentUserId = sessionRepository.currentUid()
            if (currentUserId == null) {
                Log.w("ApplicantRepository", "로그인되지 않음 - 알림 발송 불가")
                return Result.Error("로그인이 필요합니다.", IllegalStateException())
            }

            ApplicantRemote.notifyResults(applicationIds, currentUserId)
            Log.d("ApplicantRepository", "결과 알림 발송 완료: $applicationIds")
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e("ApplicantRepository", "결과 알림 발송 실패", e)
            Result.Error("결과 알림 발송에 실패했습니다.", e)
        }
    }

    override suspend fun revertToPreviousStage(applicationIds: List<String>): Result<Unit> {
        return try {
            ApplicantRemote.revertToPreviousStage(applicationIds)
            Log.d("ApplicantRepository", "이전 단계 되돌리기 완료: $applicationIds")
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e("ApplicantRepository", "이전 단계 되돌리기 실패", e)
            Result.Error("이전 단계로 되돌리기에 실패했습니다.", e)
        }
    }
}