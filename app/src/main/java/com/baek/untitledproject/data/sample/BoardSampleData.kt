package com.baek.untitledproject.data.sample

import com.baek.untitledproject.data.model.CustomQuestionResponse
import com.baek.untitledproject.data.model.InterviewSlotResponse
import com.baek.untitledproject.data.model.PostImageResponse
import com.baek.untitledproject.data.model.PostResponse
import com.baek.untitledproject.domain.data.Board
import com.baek.untitledproject.domain.data.BoardSummary
import com.google.firebase.Timestamp
import java.util.Date

object BoardSampleData {
    val boardSummaryList = listOf(
        BoardSummary("1", "예술동아리", "모집 1", "모집중"),
        BoardSummary("2", "영화동아리", "모집 2", "모집중"),
        BoardSummary("3", "00학회", "모집 3", "모집완료"),
        BoardSummary("4", "예술동아리", "모집 4", "모집중"),
        BoardSummary("5", "예술동아리", "모집 5", "모집중"),
        BoardSummary("6", "예술동아리", "모집 6", "모집중"),
        BoardSummary("7", "예술동아리", "모집 7", "모집중"),
        BoardSummary("8", "영화동아리", "모집 8", "모집중"),
        BoardSummary("9", "00학회", "모집 9", "모집완료"),
        BoardSummary("10", "예술동아리", "모집 10", "모집중"),
        BoardSummary("11", "예술동아리", "모집 11", "모집중"),
        BoardSummary("12", "예술동아리", "모집 12", "모집중")
    )

    val boardList = listOf(
        Board(
            "1", "예술동아리", "모집 1", "모집중", "2000-00-00", "2000-00-00", "본문 내용", true, listOf(
                "https://cdn.dailyvet.co.kr/wp-content/uploads/2024/05/15231647/20240515ceva_experts4.jpg",
                "https://i.namu.wiki/i/d1A_wD4kuLHmOOFqJdVlOXVt1TWA9NfNt_HA0CS0Y_N0zayUAX8olMuv7odG2FiDLDQZIRBqbPQwBSArXfEJlQ.webp",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT74p0-GFNquBzzGLN9SazH22Wrus46aPPqYQ&s"
            )
        ),
        Board(
            "2", "영화동아리", "모집 2", "모집중", "2000-00-00", "2000-00-00", "본문 내용", false, listOf(
                "https://cdn.dailyvet.co.kr/wp-content/uploads/2024/05/15231647/20240515ceva_experts4.jpg",
                "https://i.namu.wiki/i/d1A_wD4kuLHmOOFqJdVlOXVt1TWA9NfNt_HA0CS0Y_N0zayUAX8olMuv7odG2FiDLDQZIRBqbPQwBSArXfEJlQ.webp",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT74p0-GFNquBzzGLN9SazH22Wrus46aPPqYQ&s"
            )
        ),
        Board(
            "3", "00학회", "모집 3", "모집완료", "2000-00-00", "2000-00-00", "본문 내용", true, listOf(
                "https://cdn.dailyvet.co.kr/wp-content/uploads/2024/05/15231647/20240515ceva_experts4.jpg",
                "https://i.namu.wiki/i/d1A_wD4kuLHmOOFqJdVlOXVt1TWA9NfNt_HA0CS0Y_N0zayUAX8olMuv7odG2FiDLDQZIRBqbPQwBSArXfEJlQ.webp",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT74p0-GFNquBzzGLN9SazH22Wrus46aPPqYQ&s"
            )
        ),
        Board(
            "4", "예술동아리", "모집 4", "모집중", "2000-00-00", "2000-00-00", "본문 내용", false, listOf(
                "https://cdn.dailyvet.co.kr/wp-content/uploads/2024/05/15231647/20240515ceva_experts4.jpg",
                "https://i.namu.wiki/i/d1A_wD4kuLHmOOFqJdVlOXVt1TWA9NfNt_HA0CS0Y_N0zayUAX8olMuv7odG2FiDLDQZIRBqbPQwBSArXfEJlQ.webp",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT74p0-GFNquBzzGLN9SazH22Wrus46aPPqYQ&s"
            )
        ),
        Board(
            "5", "예술동아리", "모집 5", "모집중", "2000-00-00", "2000-00-00", "본문 내용", true, listOf(
                "https://cdn.dailyvet.co.kr/wp-content/uploads/2024/05/15231647/20240515ceva_experts4.jpg",
                "https://i.namu.wiki/i/d1A_wD4kuLHmOOFqJdVlOXVt1TWA9NfNt_HA0CS0Y_N0zayUAX8olMuv7odG2FiDLDQZIRBqbPQwBSArXfEJlQ.webp",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT74p0-GFNquBzzGLN9SazH22Wrus46aPPqYQ&s"
            )
        ),
        Board(
            "6", "예술동아리", "모집 6", "모집중", "2000-00-00", "2000-00-00", "본문 내용", false, listOf(
                "https://cdn.dailyvet.co.kr/wp-content/uploads/2024/05/15231647/20240515ceva_experts4.jpg",
                "https://i.namu.wiki/i/d1A_wD4kuLHmOOFqJdVlOXVt1TWA9NfNt_HA0CS0Y_N0zayUAX8olMuv7odG2FiDLDQZIRBqbPQwBSArXfEJlQ.webp",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT74p0-GFNquBzzGLN9SazH22Wrus46aPPqYQ&s"
            )
        ),
        Board("7", "예술동아리", "모집 7", "모집중", "2000-00-00", "2000-00-00", "본문 내용", true),
        Board("8", "영화동아리", "모집 8", "모집중", "2000-00-00", "2000-00-00", "본문 내용", false),
        Board("9", "00학회", "모집 9", "모집완료", "2000-00-00", "2000-00-00", "본문 내용", true),
        Board("10", "예술동아리", "모집 10", "모집중", "2000-00-00", "2000-00-00", "본문 내용", false),
        Board("11", "예술동아리", "모집 11", "모집중", "2000-00-00", "2000-00-00", "본문 내용", true),
        Board("12", "예술동아리", "모집 12", "모집중", "2000-00-00", "2000-00-00", "본문 내용", false)
    )

    val postList = listOf(
        PostResponse(
            post_id = "1",
            author_user_id = "user_001",
            title = "프로그래밍 동아리 신입 모집",
            organization = "코딩클럽",
            content = "함께 코딩하며 성장할 신입 부원을 모집합니다!",
            recruitment_start = Timestamp.now(),
            recruitment_end = Timestamp.now(),
            has_interview = true,
            status = "recruiting",
            requires_name = true,
            requires_student_id = true,
            requires_department = false,
            requires_gender = false,
            requires_age = false,
            requires_phone = true,
            author_name = "김철수",
            author_organization = "코딩클럽",
            created_at = Timestamp.now(),
            updated_at = Timestamp.now()
        ),
        PostResponse(
            post_id = "2",
            author_user_id = "user_002",
            title = "사진 동아리 신입 모집",
            organization = "사진사랑",
            content = "사진에 관심 있는 분들을 기다립니다!",
            recruitment_start = Timestamp.now(),
            recruitment_end = Timestamp.now(),
            has_interview = false,
            status = "recruiting",
            requires_name = true,
            requires_student_id = false,
            requires_department = true,
            requires_gender = false,
            requires_age = true,
            requires_phone = true,
            author_name = "박민수",
            author_organization = "사진사랑",
            created_at = Timestamp.now(),
            updated_at = Timestamp.now()
        ),
        PostResponse(
            post_id = "3",
            author_user_id = "user_003",
            title = "밴드부 보컬 모집",
            organization = "소리날밴드",
            content = "음악을 사랑하는 사람, 지금 지원하세요!\n음악을 사랑하는 사람, 지금 지원하세요!\n음악을 사랑하는 사람, 지금 지원하세요!\n음악을 사랑하는 사람, 지금 지원하세요!\n음악을 사랑하는 사람, 지금 지원하세요!\n음악을 사랑하는 사람, 지금 지원하세요!\n음악을 사랑하는 사람, 지금 지원하세요!\n음악을 사랑하는 사람, 지금 지원하세요!\n음악을 사랑하는 사람, 지금 지원하세요!\n음악을 사랑하는 사람, 지금 지원하세요!\n음악을 사랑하는 사람, 지금 지원하세요!\n음악을 사랑하는 사람, 지금 지원하세요!\n음악을 사랑하는 사람, 지금 지원하세요!\n음악을 사랑하는 사람, 지금 지원하세요!\n음악을 사랑하는 사람, 지금 지원하세요!\n음악을 사랑하는 사람, 지금 지원하세요!\n음악을 사랑하는 사람, 지금 지원하세요!\n음악을 사랑하는 사람, 지금 지원하세요!\n음악을 사랑하는 사람, 지금 지원하세요!\n음악을 사랑하는 사람, 지금 지원하세요!\n음악을 사랑하는 사람, 지금 지원하세요!\n음악을 사랑하는 사람, 지금 지원하세요!\n음악을 사랑하는 사람, 지금 지원하세요!\n음악을 사랑하는 사람, 지금 지원하세요!\n음악을 사랑하는 사람, 지금 지원하세요!\n음악을 사랑하는 사람, 지금 지원하세요!\n음악을 사랑하는 사람, 지금 지원하세요!\n음악을 사랑하는 사람, 지금 지원하세요!\n음악을 사랑하는 사람, 지금 지원하세요!\n음악을 사랑하는 사람, 지금 지원하세요!\n음악을 사랑하는 사람, 지금 지원하세요!\n음악을 사랑하는 사람, 지금 지원하세요!\n음악을 사랑하는 사람, 지금 지원하세요!\n음악을 사랑하는 사람, 지금 지원하세요!\n음악을 사랑하는 사람, 지금 지원하세요!",
            recruitment_start = Timestamp.now(),
            recruitment_end = Timestamp.now(),
            has_interview = true,
            status = "closed",
            requires_name = true,
            requires_student_id = true,
            requires_department = true,
            requires_gender = false,
            requires_age = false,
            requires_phone = true,
            author_name = "이소연",
            author_organization = "소리날밴드",
            created_at = Timestamp.now(),
            updated_at = Timestamp.now()
        )
    )

    val images = listOf(
        PostImageResponse(
            "https://cdn.dailyvet.co.kr/wp-content/uploads/2024/05/15231647/20240515ceva_experts4.jpg",
            0
        ),
        PostImageResponse(
            "https://i.namu.wiki/i/d1A_wD4kuLHmOOFqJdVlOXVt1TWA9NfNt_HA0CS0Y_N0zayUAX8olMuv7odG2FiDLDQZIRBqbPQwBSArXfEJlQ.webp",
            1
        ),
        PostImageResponse(
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT74p0-GFNquBzzGLN9SazH22Wrus46aPPqYQ&s",
            2
        ),

    )
    val interviewSlots = listOf(
        InterviewSlotResponse(
            interview_date = Timestamp(Date(2025 - 1900, 7, 1)), // 2025-08-01
            interview_time = "14:00",
            max_capacity = 3,
            current_reservations = 1
        ),
        InterviewSlotResponse(
            interview_date = Timestamp(Date(2025 - 1900, 7, 1)), // 2025-08-01
            interview_time = "15:00",
            max_capacity = 3,
            current_reservations = 0
        ),
        InterviewSlotResponse(
            interview_date = Timestamp(Date(2025 - 1900, 7, 2)), // 2025-08-02
            interview_time = "10:00",
            max_capacity = 2,
            current_reservations = 2
        )
    )

    val customQuestions = listOf(
        CustomQuestionResponse(
            question_id = "1",
            question_text = "quetion1",
            question_order = 1
        ),
        CustomQuestionResponse(
            question_id = "2",
            question_text = "quetion2",
            question_order = 2
        ),
        CustomQuestionResponse(
            question_id = "3",
            question_text = "quetion3",
            question_order = 3
        )
    )

}