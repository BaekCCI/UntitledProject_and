package com.baek.untitledproject.domain.repository

import com.baek.untitledproject.domain.data.AppliedRecruitSummary
import com.baek.untitledproject.domain.data.MyRecruitSummary
import com.baek.untitledproject.domain.data.ScheduleGroupSummary

interface MyRecruitsRepository {

    /**
     * 내가 올린 공고 목록 조회
     */
    suspend fun getMyRecruits(): List<MyRecruitSummary>

    /**
     * 내가 지원한 공고 목록 조회
     */
    suspend fun getAppliedRecruits(): List<AppliedRecruitSummary>

    /**
     * 다가오는 면접 일정 그룹 조회
     */
    suspend fun getScheduleGroups(): List<ScheduleGroupSummary>
}