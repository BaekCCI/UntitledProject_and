package com.baek.untitledproject.domain.repository

import com.baek.untitledproject.domain.data.ApplicantSummary
import com.baek.untitledproject.domain.data.Application
import com.baek.untitledproject.domain.utils.Result

/**
 * 지원자 관리 Repository 인터페이스
 * Firebase applications 컬렉션 기준으로 수정
 */
interface ApplicantRepository {

    /**
     * 특정 공고의 지원자 목록 조회
     */
    suspend fun getApplicants(postId: String): List<ApplicantSummary>

    /**
     * 지원자 상세 정보 조회
     */
    suspend fun getApplicantDetail(applicationId: String): ApplicantSummary

    /**
     * 특정 공고에 면접 일정이 설정되어 있는지 확인
     */
    suspend fun hasInterviewSlots(postId: String): Result<Boolean>

    /**
     * 지원자들을 면접 대기 상태로 변경
     */
    suspend fun scheduleInterviews(applicationIds: List<String>): Result<Unit>

    /**
     * 지원자들의 면접을 완료 상태로 변경
     */
    suspend fun completeInterviews(applicationIds: List<String>): Result<Unit>

    /**
     * 지원자들을 합격 처리
     */
    suspend fun passApplicants(applicationIds: List<String>): Result<Unit>

    /**
     * 지원자들을 불합격 처리
     */
    suspend fun failApplicants(applicationIds: List<String>): Result<Unit>

    /**
     * 지원자들에게 결과 알림 발송
     */
    suspend fun notifyResults(applicationIds: List<String>): Result<Unit>
}