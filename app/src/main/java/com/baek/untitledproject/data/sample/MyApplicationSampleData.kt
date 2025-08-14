package com.baek.untitledproject.data.sample

import com.baek.untitledproject.data.model.ApplicationResponse
import com.google.firebase.Timestamp
import java.util.Date

object MyApplicationSampleData {

    // 내가 지원한 공고들 (Firebase ApplicationResponse 구조)
    val myApplicationList = listOf(
        ApplicationResponse(
            application_id = "my_app_001",
            applicant_user_id = "user_author_001", // 현재 사용자가 지원자
            post_id = "other_post_001",
            status = "submitted",
            is_passed = null,
            interview_slot_id = null,
            interview_reservation_status = null,

            // 지원자 정보 (내 정보)
            applicant_name = "김철수",
            applicant_birth_year = 2001,
            applicant_gender = "M",
            applicant_department = "컴퓨터공학과",
            applicant_student_id = "202112345",
            applicant_phone = "010-1234-5678",

            // 공고 정보 (지원한 공고)
            post_title = "사진 동아리 신입 모집",
            post_organization = "사진사랑",
            post_author_user_id = "user_other_001",

            applied_at = Timestamp(Date(System.currentTimeMillis() - 86400000 * 3)), // 3일 전 지원
            updated_at = Timestamp(Date(System.currentTimeMillis() - 86400000 * 3))
        ),

        ApplicationResponse(
            application_id = "my_app_002",
            applicant_user_id = "user_author_001",
            post_id = "other_post_002",
            status = "interview_waiting",
            is_passed = null,
            interview_slot_id = "slot_001",
            interview_reservation_status = "reserved",

            applicant_name = "김철수",
            applicant_birth_year = 2001,
            applicant_gender = "M",
            applicant_department = "컴퓨터공학과",
            applicant_student_id = "202112345",
            applicant_phone = "010-1234-5678",

            post_title = "밴드부 베이시스트 모집",
            post_organization = "소리날밴드",
            post_author_user_id = "user_other_002",

            applied_at = Timestamp(Date(System.currentTimeMillis() - 86400000 * 5)), // 5일 전 지원
            updated_at = Timestamp(Date(System.currentTimeMillis() - 86400000 * 1)) // 1일 전 면접 확정
        ),

        ApplicationResponse(
            application_id = "my_app_003",
            applicant_user_id = "user_author_001",
            post_id = "other_post_003",
            status = "review_completed",
            is_passed = true,
            interview_slot_id = "slot_002",
            interview_reservation_status = "completed",

            applicant_name = "김철수",
            applicant_birth_year = 2001,
            applicant_gender = "M",
            applicant_department = "컴퓨터공학과",
            applicant_student_id = "202112345",
            applicant_phone = "010-1234-5678",

            post_title = "영상 편집 동아리 모집",
            post_organization = "영상제작소",
            post_author_user_id = "user_other_003",

            applied_at = Timestamp(Date(System.currentTimeMillis() - 86400000 * 10)), // 10일 전 지원
            updated_at = Timestamp(Date(System.currentTimeMillis() - 86400000 * 2)) // 2일 전 합격
        ),

        ApplicationResponse(
            application_id = "my_app_004",
            applicant_user_id = "user_author_001",
            post_id = "other_post_004",
            status = "review_completed",
            is_passed = false,
            interview_slot_id = "slot_003",
            interview_reservation_status = "completed",

            applicant_name = "김철수",
            applicant_birth_year = 2001,
            applicant_gender = "M",
            applicant_department = "컴퓨터공학과",
            applicant_student_id = "202112345",
            applicant_phone = "010-1234-5678",

            post_title = "댄스 동아리 신입 모집",
            post_organization = "댄스크루",
            post_author_user_id = "user_other_004",

            applied_at = Timestamp(Date(System.currentTimeMillis() - 86400000 * 12)), // 12일 전 지원
            updated_at = Timestamp(Date(System.currentTimeMillis() - 86400000 * 3)) // 3일 전 불합격
        )
    )
}