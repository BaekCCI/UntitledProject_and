package com.baek.untitledproject.data.repository

import android.util.Log
import com.baek.untitledproject.data.remote.MyRecruitsRemote
import com.baek.untitledproject.domain.data.AppliedRecruitSummary
import com.baek.untitledproject.domain.data.MyRecruitSummary
import com.baek.untitledproject.domain.data.ScheduleGroupSummary
import com.baek.untitledproject.domain.repository.MyRecruitsRepository
import javax.inject.Inject

class MyRecruitsRepositoryImpl @Inject constructor() : MyRecruitsRepository {

    override suspend fun getMyRecruits(): List<MyRecruitSummary> {
        return try {
            MyRecruitsRemote.getMyRecruits()
        } catch (e: Exception) {
            Log.e("MyRecruitsRepository", "내 공고 목록 조회 실패", e)
            emptyList()
        }
    }

    override suspend fun getAppliedRecruits(): List<AppliedRecruitSummary> {
        return try {
            MyRecruitsRemote.getAppliedRecruits()
        } catch (e: Exception) {
            Log.e("MyRecruitsRepository", "지원한 공고 목록 조회 실패", e)
            emptyList()
        }
    }

    override suspend fun getScheduleGroups(): List<ScheduleGroupSummary> {
        return try {
            MyRecruitsRemote.getScheduleGroups()
        } catch (e: Exception) {
            Log.e("MyRecruitsRepository", "면접 일정 조회 실패", e)
            emptyList()
        }
    }
}