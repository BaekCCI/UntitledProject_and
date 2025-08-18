package com.baek.untitledproject.data.repository

import android.util.Log
import com.baek.untitledproject.data.remote.ApplicantRemote
import com.baek.untitledproject.domain.data.ApplicantSummary
import com.baek.untitledproject.domain.repository.ApplicantRepository
import com.baek.untitledproject.domain.utils.Result
import javax.inject.Inject


class ApplicantRepositoryImpl @Inject constructor() : ApplicantRepository {

    // ApplicantRepositoryImpl.kt 로그 추가
    override suspend fun getApplicants(postId: String): List<ApplicantSummary> {
        return try {
            val result = ApplicantRemote.getApplicants(postId)
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

    override suspend fun scheduleInterviews(applicationIds: List<String>): Result<Unit> {
        return try {
            ApplicantRemote.scheduleInterviews(applicationIds)
            Log.d("ApplicantRepository", "면접 일정 설정 완료: $applicationIds")
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e("ApplicantRepository", "면접 일정 설정 실패", e)
            Result.Error("면접 일정 설정에 실패했습니다.", e)
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
            ApplicantRemote.notifyResults(applicationIds)
            Log.d("ApplicantRepository", "결과 알림 발송 완료: $applicationIds")
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e("ApplicantRepository", "결과 알림 발송 실패", e)
            Result.Error("결과 알림 발송에 실패했습니다.", e)
        }
    }
}