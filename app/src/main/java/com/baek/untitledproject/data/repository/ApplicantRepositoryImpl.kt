package com.baek.untitledproject.data.repository

import com.baek.untitledproject.data.sample.ApplicantSampleData
import com.baek.untitledproject.domain.data.ApplicantSummary
import com.baek.untitledproject.domain.repository.ApplicantRepository
import javax.inject.Inject

class ApplicantRepositoryImpl @Inject constructor() : ApplicantRepository {

    override suspend fun getApplicants(recruitId: String): List<ApplicantSummary> {
        return ApplicantSampleData.applicantList
    }

    override suspend fun scheduleInterviews(applicantIds: List<String>): Boolean {
        // TODO: 실제 API 호출하여 면접 일정 등록
        return true
    }

    override suspend fun completeInterviews(applicantIds: List<String>): Boolean {
        // TODO: 실제 API 호출하여 면접 완료 처리
        return true
    }

    override suspend fun passApplicants(applicantIds: List<String>): Boolean {
        // TODO: 실제 API 호출하여 합격 처리
        return true
    }

    override suspend fun failApplicants(applicantIds: List<String>): Boolean {
        // TODO: 실제 API 호출하여 불합격 처리
        return true
    }

    override suspend fun notifyResults(applicantIds: List<String>): Boolean {
        // TODO: 실제 API 호출하여 결과 알림 발송
        return true
    }
}