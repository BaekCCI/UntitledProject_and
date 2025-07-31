package com.baek.untitledproject.data.sample

import com.baek.untitledproject.domain.data.MyRecruitSummary

object MyRecruitSampleData {

    val myRecruitList = listOf(
        MyRecruitSummary(
            id = "my1",
            category = "영상동아리",
            title = "영상동아리 OO 모집합니다.",
            recruitStatus = "모집중",
            thumbnailUrl = "https://cdn.dailyvet.co.kr/wp-content/uploads/2024/05/15231647/20240515ceva_experts4.jpg",
            imageUrls = listOf(
                "https://cdn.dailyvet.co.kr/wp-content/uploads/2024/05/15231647/20240515ceva_experts4.jpg",
                "https://i.namu.wiki/i/d1A_wD4kuLHmOOFqJdVlOXVt1TWA9NfNt_HA0CS0Y_N0zayUAX8olMuv7odG2FiDLDQZIRBqbPQwBSArXfEJlQ.webp",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT74p0-GFNquBzzGLN9SazH22Wrus46aPPqYQ&s"
            ),
            currentImageIndex = 0,
            applicantCount = 15,
            interviewCount = 8
        ),
        MyRecruitSummary(
            id = "my2",
            category = "영상동아리",
            title = "영상동아리 신입 모집",
            recruitStatus = "모집완료",
            thumbnailUrl = "https://i.namu.wiki/i/d1A_wD4kuLHmOOFqJdVlOXVt1TWA9NfNt_HA0CS0Y_N0zayUAX8olMuv7odG2FiDLDQZIRBqbPQwBSArXfEJlQ.webp",
            imageUrls = listOf(
                "https://i.namu.wiki/i/d1A_wD4kuLHmOOFqJdVlOXVt1TWA9NfNt_HA0CS0Y_N0zayUAX8olMuv7odG2FiDLDQZIRBqbPQwBSArXfEJlQ.webp"
            ),
            currentImageIndex = 0,
            applicantCount = 24,
            interviewCount = 12
        )
    )
}