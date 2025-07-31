package com.baek.untitledproject.data.repository

import com.baek.untitledproject.data.sample.AppliedRecruitSampleData
import com.baek.untitledproject.data.sample.MyRecruitSampleData
import com.baek.untitledproject.data.sample.ScheduleSampleData
import com.baek.untitledproject.domain.data.AppliedRecruitSummary
import com.baek.untitledproject.domain.data.MyRecruitSummary
import com.baek.untitledproject.domain.data.ScheduleGroupSummary
import com.baek.untitledproject.domain.repository.MyRecruitsRepository
import javax.inject.Inject

class MyRecruitsRepositoryImpl @Inject constructor() : MyRecruitsRepository {

    override suspend fun getScheduleGroups(): List<ScheduleGroupSummary> {
        return ScheduleSampleData.scheduleGroupList
    }

    override suspend fun getMyRecruits(): List<MyRecruitSummary> {
        return MyRecruitSampleData.myRecruitList
    }

    override suspend fun getAppliedRecruits(): List<AppliedRecruitSummary> {
        return AppliedRecruitSampleData.appliedRecruitList
    }
}