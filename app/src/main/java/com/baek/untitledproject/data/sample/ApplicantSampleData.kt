package com.baek.untitledproject.data.sample

import com.baek.untitledproject.data.model.ApplicationResponse
import com.google.firebase.Timestamp
import java.util.Date

object ApplicationSampleData {

    val applicationList = listOf(
        // 지원서 제출 완료 상태
        ApplicationResponse(
            application_id = "app_001",
            applicant_user_id = "user_001",
            post_id = "my_post_001",
            status = "submitted",
            is_passed = null,
            interview_slot_id = null,
            interview_reservation_status = null,

            applicant_name = "김민수",
            applicant_birth_year = 2002,
            applicant_gender = "M",
            applicant_department = "컴퓨터공학과",
            applicant_student_id = "202012345",
            applicant_phone = "010-1234-5678",

            post_title = "프로그래밍 동아리 신입 모집",
            post_organization = "코딩클럽",
            post_author_user_id = "user_author_001",

            applied_at = Timestamp.now(),
            updated_at = Timestamp.now()
        ),

        // 면접 대기중 상태
        ApplicationResponse(
            application_id = "app_002",
            applicant_user_id = "user_002",
            post_id = "my_post_001",
            status = "interview_waiting",
            is_passed = null,
            interview_slot_id = "slot_001",
            interview_reservation_status = "reserved",

            applicant_name = "이영희",
            applicant_birth_year = 2001,
            applicant_gender = "F",
            applicant_department = "경영학과",
            applicant_student_id = "202112346",
            applicant_phone = "010-9876-5432",

            post_title = "프로그래밍 동아리 신입 모집",
            post_organization = "코딩클럽",
            post_author_user_id = "user_author_001",

            applied_at = Timestamp(Date(System.currentTimeMillis() - 86400000)), // 1일 전
            updated_at = Timestamp.now()
        ),

        // 심사 대기중 상태
        ApplicationResponse(
            application_id = "app_003",
            applicant_user_id = "user_003",
            post_id = "my_post_001",
            status = "review_waiting",
            is_passed = null,
            interview_slot_id = "slot_002",
            interview_reservation_status = "completed",

            applicant_name = "박철수",
            applicant_birth_year = 2000,
            applicant_gender = "M",
            applicant_department = "전자공학과",
            applicant_student_id = "202012347",
            applicant_phone = "010-5555-1234",

            post_title = "프로그래밍 동아리 신입 모집",
            post_organization = "코딩클럽",
            post_author_user_id = "user_author_001",

            applied_at = Timestamp(Date(System.currentTimeMillis() - 172800000)), // 2일 전
            updated_at = Timestamp(Date(System.currentTimeMillis() - 86400000)) // 1일 전
        ),

        // 합격 상태
        ApplicationResponse(
            application_id = "app_004",
            applicant_user_id = "user_004",
            post_id = "my_post_0012",
            status = "review_completed",
            is_passed = true,
            interview_slot_id = "slot_003",
            interview_reservation_status = "completed",

            applicant_name = "최지은",
            applicant_birth_year = 2002,
            applicant_gender = "F",
            applicant_department = "디자인학과",
            applicant_student_id = "202212348",
            applicant_phone = "010-7777-8888",

            post_title = "사진 동아리 신입 모집",
            post_organization = "사진사랑",
            post_author_user_id = "user_author_002",

            applied_at = Timestamp(Date(System.currentTimeMillis() - 259200000)), // 3일 전
            updated_at = Timestamp(Date(System.currentTimeMillis() - 86400000)) // 1일 전
        ),

        // 불합격 상태
        ApplicationResponse(
            application_id = "app_005",
            applicant_user_id = "user_005",
            post_id = "my_post_001",
            status = "review_completed",
            is_passed = false,
            interview_slot_id = null,
            interview_reservation_status = null,

            applicant_name = "정민호",
            applicant_birth_year = 2001,
            applicant_gender = "M",
            applicant_department = "수학과",
            applicant_student_id = "202112349",
            applicant_phone = null,

            post_title = "사진 동아리 신입 모집",
            post_organization = "사진사랑",
            post_author_user_id = "user_author_002",

            applied_at = Timestamp(Date(System.currentTimeMillis() - 345600000)), // 4일 전
            updated_at = Timestamp(Date(System.currentTimeMillis() - 172800000)) // 2일 전
        ),

        // 다른 공고 지원자들
        ApplicationResponse(
            application_id = "app_006",
            applicant_user_id = "user_006",
            post_id = "my_post_001",
            status = "submitted",
            is_passed = null,
            interview_slot_id = null,
            interview_reservation_status = null,

            applicant_name = "한소영",
            applicant_birth_year = 2003,
            applicant_gender = "F",
            applicant_department = "심리학과",
            applicant_student_id = "202312350",
            applicant_phone = "010-2222-3333",

            post_title = "밴드부 보컬 모집",
            post_organization = "소리날밴드",
            post_author_user_id = "user_author_003",

            applied_at = Timestamp.now(),
            updated_at = Timestamp.now()
        )
    )

    // 특정 공고의 지원자들만 필터링하는 함수
    fun getApplicationsByPostId(postId: String): List<ApplicationResponse> {
        return applicationList.filter { it.post_id == postId }
    }

    // 특정 사용자의 지원서들만 필터링하는 함수
    fun getApplicationsByUserId(userId: String): List<ApplicationResponse> {
        return applicationList.filter { it.applicant_user_id == userId }
    }
}