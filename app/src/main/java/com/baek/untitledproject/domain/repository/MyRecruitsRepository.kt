package com.baek.untitledproject.domain.repository

import com.baek.untitledproject.domain.data.AppliedRecruitSummary
import com.baek.untitledproject.domain.data.InterviewSlot
import com.baek.untitledproject.domain.data.MyRecruitSummary
import com.baek.untitledproject.domain.data.ScheduleGroupSummary

interface MyRecruitsRepository {

    suspend fun getMyRecruits(): List<MyRecruitSummary>

    suspend fun getAppliedRecruits(): List<AppliedRecruitSummary>

    suspend fun getScheduleGroups(): List<ScheduleGroupSummary>

    suspend fun getAvailableInterviewSlots(postId: String): List<InterviewSlot>

    suspend fun reserveInterviewSlot(applicationId: String, slotId: String): Boolean

    suspend fun cancelInterviewReservation(applicationId: String): Boolean
}