package com.baek.untitledproject.data.sample

import com.baek.untitledproject.data.model.PostResponse
import com.google.firebase.Timestamp
import java.util.Date

object MyPostSampleData {

    // 내가 올린 공고들 (Firebase PostResponse 구조)
    val myPostList = listOf(
        PostResponse(
            post_id = "my_post_001",
            author_user_id = "user_author_001",
            title = "프로그래밍 동아리 신입 모집",
            organization = "코딩클럽",
            content = "함께 코딩하며 성장할 신입 부원을 모집합니다! Java, Kotlin, React 등 다양한 기술을 함께 학습하고 프로젝트를 진행해요.",
            recruitment_start = Timestamp(Date(System.currentTimeMillis() - 86400000 * 7)), // 7일 전
            recruitment_end = Timestamp(Date(System.currentTimeMillis() + 86400000 * 7)), // 7일 후
            has_interview = true,
            status = "recruiting",
            requires_name = true,
            requires_student_id = true,
            requires_department = false,
            requires_gender = false,
            requires_age = false,
            //requires_phone = true,
            author_name = "김철수",
            author_organization = "코딩클럽",
            created_at = Timestamp(Date(System.currentTimeMillis() - 86400000 * 10)), // 10일 전 작성
            updated_at = Timestamp(Date(System.currentTimeMillis() - 86400000 * 2)) // 2일 전 수정
        ),

        PostResponse(
            post_id = "my_post_002",
            author_user_id = "user_author_001",
            title = "웹 개발 스터디 멤버 모집",
            organization = "웹스터디",
            content = "React, Node.js를 함께 공부할 스터디 멤버를 모집합니다. 초보자도 환영!",
            recruitment_start = Timestamp(Date(System.currentTimeMillis() - 86400000 * 5)), // 5일 전
            recruitment_end = Timestamp(Date(System.currentTimeMillis() + 86400000 * 10)), // 10일 후
            has_interview = false,
            status = "recruiting",
            requires_name = true,
            requires_student_id = false,
            requires_department = true,
            requires_gender = false,
            requires_age = false,
            //requires_phone = false,
            author_name = "김철수",
            author_organization = "웹스터디",
            created_at = Timestamp(Date(System.currentTimeMillis() - 86400000 * 8)), // 8일 전 작성
            updated_at = Timestamp(Date(System.currentTimeMillis() - 86400000 * 1)) // 1일 전 수정
        ),

        PostResponse(
            post_id = "my_post_003",
            author_user_id = "user_author_001",
            title = "앱 개발 프로젝트 팀원 모집",
            organization = "앱팩토리",
            content = "Android, iOS 앱 개발 프로젝트에 참여할 팀원을 모집합니다. 포트폴리오 제작 기회!",
            recruitment_start = Timestamp(Date(System.currentTimeMillis() - 86400000 * 15)), // 15일 전
            recruitment_end = Timestamp(Date(System.currentTimeMillis() - 86400000 * 1)), // 1일 전 마감
            has_interview = true,
            status = "completed", // 모집완료
            requires_name = true,
            requires_student_id = true,
            requires_department = true,
            requires_gender = false,
            requires_age = true,
            //requires_phone = true,
            author_name = "김철수",
            author_organization = "앱팩토리",
            created_at = Timestamp(Date(System.currentTimeMillis() - 86400000 * 20)), // 20일 전 작성
            updated_at = Timestamp(Date(System.currentTimeMillis() - 86400000 * 2)) // 2일 전 수정
        )
    )
}