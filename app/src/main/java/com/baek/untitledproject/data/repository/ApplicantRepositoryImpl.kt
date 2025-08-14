package com.baek.untitledproject.data.repository

import android.util.Log
import com.baek.untitledproject.data.model.mapper.toApplicantSummary
import com.baek.untitledproject.data.sample.ApplicationSampleData
import com.baek.untitledproject.domain.data.ApplicantSummary
import com.baek.untitledproject.domain.repository.ApplicantRepository
import com.baek.untitledproject.domain.utils.Result
import javax.inject.Inject

/**
 * 지원자 관리 Repository 구현체
 * 현재는 Sample 데이터 사용, 추후 Firestore 연동
 */
class ApplicantRepositoryImpl @Inject constructor() : ApplicantRepository {

    override suspend fun getApplicants(postId: String): List<ApplicantSummary> {
        return try {
            val applications = ApplicationSampleData.getApplicationsByPostId(postId)
            applications.map { it.toApplicantSummary() }
        } catch (e: Exception) {
            Log.e("ApplicantRepository", "지원자 목록 조회 실패", e)
            emptyList()
        }
    }

    override suspend fun getApplicantDetail(applicationId: String): ApplicantSummary {
        return try {
            val application = ApplicationSampleData.applicationList
                .find { it.application_id == applicationId }
                ?: throw IllegalArgumentException("지원서를 찾을 수 없습니다: $applicationId")

            application.toApplicantSummary()
        } catch (e: Exception) {
            Log.e("ApplicantRepository", "지원자 상세 조회 실패", e)
            throw e
        }
    }

    override suspend fun scheduleInterviews(applicationIds: List<String>): Result<Unit> {
        return try {
            // TODO: Sample 데이터 업데이트 로직
            // 실제 구현시에는 Firestore에서 status를 "interview_waiting"으로 변경
            Log.d("ApplicantRepository", "면접 일정 설정: $applicationIds")
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e("ApplicantRepository", "면접 일정 설정 실패", e)
            Result.Error("면접 일정 설정에 실패했습니다.", e)
        }
    }

    override suspend fun completeInterviews(applicationIds: List<String>): Result<Unit> {
        return try {
            // TODO: Sample 데이터 업데이트 로직
            // 실제 구현시에는 Firestore에서 status를 "review_waiting"으로 변경
            Log.d("ApplicantRepository", "면접 완료 처리: $applicationIds")
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e("ApplicantRepository", "면접 완료 처리 실패", e)
            Result.Error("면접 완료 처리에 실패했습니다.", e)
        }
    }

    override suspend fun passApplicants(applicationIds: List<String>): Result<Unit> {
        return try {
            // TODO: Sample 데이터 업데이트 로직
            // 실제 구현시에는 Firestore에서 status를 "review_completed", is_passed를 true로 변경
            Log.d("ApplicantRepository", "합격 처리: $applicationIds")
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e("ApplicantRepository", "합격 처리 실패", e)
            Result.Error("합격 처리에 실패했습니다.", e)
        }
    }

    override suspend fun failApplicants(applicationIds: List<String>): Result<Unit> {
        return try {
            // TODO: Sample 데이터 업데이트 로직
            // 실제 구현시에는 Firestore에서 status를 "review_completed", is_passed를 false로 변경
            Log.d("ApplicantRepository", "불합격 처리: $applicationIds")
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e("ApplicantRepository", "불합격 처리 실패", e)
            Result.Error("불합격 처리에 실패했습니다.", e)
        }
    }

    override suspend fun notifyResults(applicationIds: List<String>): Result<Unit> {
        return try {
            // TODO: 알림 발송 로직
            // 실제 구현시에는 notifications 컬렉션에 알림 추가
            Log.d("ApplicantRepository", "결과 알림 발송: $applicationIds")
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e("ApplicantRepository", "결과 알림 발송 실패", e)
            Result.Error("결과 알림 발송에 실패했습니다.", e)
        }
    }
}