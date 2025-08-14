package com.baek.untitledproject.data.repository

import android.util.Log
import com.baek.untitledproject.data.model.mapper.toMyRecruitSummary
import com.baek.untitledproject.data.model.mapper.toAppliedRecruitSummary
import com.baek.untitledproject.data.sample.MyPostSampleData
import com.baek.untitledproject.data.sample.MyApplicationSampleData
import com.baek.untitledproject.data.sample.ApplicationSampleData
import com.baek.untitledproject.domain.data.AppliedRecruitSummary
import com.baek.untitledproject.domain.data.MyRecruitSummary
import com.baek.untitledproject.domain.data.ScheduleGroupSummary
import com.baek.untitledproject.domain.data.ScheduleItemSummary
import com.baek.untitledproject.domain.repository.MyRecruitsRepository
import javax.inject.Inject

/**
 * 내 공고/지원한 공고 Repository 구현체
 * 현재는 Sample 데이터 사용, 추후 Firestore 연동
 */
class MyRecruitsRepositoryImpl @Inject constructor() : MyRecruitsRepository {

    override suspend fun getMyRecruits(): List<MyRecruitSummary> {
        return try {
            // 내가 작성한 공고들 (새로운 Sample 데이터 사용)
            val myPosts = MyPostSampleData.myPostList

            myPosts.map { post ->
                // 각 공고의 지원자 수 계산 (ApplicationSampleData에서)
                val applicantCount = ApplicationSampleData.getApplicationsByPostId(post.post_id).size

                // TODO: 첫 번째 이미지 가져오기 (post_images 컬렉션에서)
                val thumbnailUrl: String? = null

                post.toMyRecruitSummary(
                    thumbnailUrl = thumbnailUrl,
                    applicantCount = applicantCount
                )
            }
        } catch (e: Exception) {
            Log.e("MyRecruitsRepository", "내 공고 목록 조회 실패", e)
            emptyList()
        }
    }

    override suspend fun getAppliedRecruits(): List<AppliedRecruitSummary> {
        return try {
            // 내가 지원한 지원서들 (새로운 Sample 데이터 사용)
            val myApplications = MyApplicationSampleData.myApplicationList

            myApplications.map { application ->
                // 해당 공고 정보는 이미 비정규화되어 있음
                val recruitStatus = "recruiting" // 임시값, 실제로는 posts에서 조회
                val hasInterview = true // 임시값, 실제로는 posts에서 조회

                application.toAppliedRecruitSummary(
                    recruitStatus = recruitStatus,
                    hasInterview = hasInterview
                )
            }
        } catch (e: Exception) {
            Log.e("MyRecruitsRepository", "지원한 공고 목록 조회 실패", e)
            emptyList()
        }
    }

    override suspend fun getScheduleGroups(): List<ScheduleGroupSummary> {
        return try {
            // TODO: 실제로는 interview_slots와 applications 조인해서 내 면접 일정 가져오기
            // 현재는 임시 데이터 반환
            listOf(
                ScheduleGroupSummary(
                    id = "group_today",
                    title = "오늘",
                    count = 2,
                    isExpanded = true,
                    scheduleItems = listOf(
                        ScheduleItemSummary(
                            id = "schedule_1",
                            date = "08/13",
                            time = "14:00",
                            organization = "코딩클럽"
                        ),
                        ScheduleItemSummary(
                            id = "schedule_2",
                            date = "08/13",
                            time = "16:00",
                            organization = "사진사랑"
                        )
                    )
                ),
                ScheduleGroupSummary(
                    id = "group_tomorrow",
                    title = "내일",
                    count = 1,
                    isExpanded = false,
                    scheduleItems = listOf(
                        ScheduleItemSummary(
                            id = "schedule_3",
                            date = "08/14",
                            time = "10:00",
                            organization = "소리날밴드"
                        )
                    )
                )
            )
        } catch (e: Exception) {
            Log.e("MyRecruitsRepository", "면접 일정 조회 실패", e)
            emptyList()
        }
    }
}