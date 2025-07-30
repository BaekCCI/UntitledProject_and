package com.baek.untitledproject.domain.repository

import com.baek.untitledproject.domain.data.AppliedRecruitSummary
import com.baek.untitledproject.domain.data.MyRecruitSummary
import com.baek.untitledproject.domain.data.ScheduleGroupSummary

interface MyRecruitsRepository {
    suspend fun getScheduleGroups(): List<ScheduleGroupSummary>
    suspend fun getMyRecruits(): List<MyRecruitSummary>
    suspend fun getAppliedRecruits(): List<AppliedRecruitSummary>
}