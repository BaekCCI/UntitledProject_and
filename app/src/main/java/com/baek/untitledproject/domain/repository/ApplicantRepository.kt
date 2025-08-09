package com.baek.untitledproject.domain.repository

import com.baek.untitledproject.domain.data.ApplicantSummary

interface ApplicantRepository {

    suspend fun getApplicants(recruitId: String): List<ApplicantSummary>

    suspend fun scheduleInterviews(applicantIds: List<String>): Boolean

    suspend fun completeInterviews(applicantIds: List<String>): Boolean

    suspend fun passApplicants(applicantIds: List<String>): Boolean

    suspend fun failApplicants(applicantIds: List<String>): Boolean

    suspend fun notifyResults(applicantIds: List<String>): Boolean
}