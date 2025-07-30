package com.baek.untitledproject.data.sample

import com.baek.untitledproject.domain.data.Board
import com.baek.untitledproject.domain.data.BoardSummary

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
}