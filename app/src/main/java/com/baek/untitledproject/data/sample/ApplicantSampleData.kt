package com.baek.untitledproject.data.sample

import com.baek.untitledproject.domain.data.ApplicantSummary

object ApplicantSampleData {

    val applicantList = listOf(
        ApplicantSummary(
            id = "app1",
            name = "제이름",
            gender = "남",
            age = 24,
            department = "커뮤니케이션학과",
            status = "submitted",
            statusText = "지원서 제출 완료",
            applicationDate = "2024-08-15",
            phoneNumber = "010-1234-5678",
            email = "applicant1@example.com"
        ),
        ApplicantSummary(
            id = "app2",
            name = "헤리",
            gender = "여",
            age = 24,
            department = "커뮤니케이션학과",
            status = "submitted",
            statusText = "지원서 제출 완료",
            applicationDate = "2024-08-16",
            phoneNumber = "010-2345-6789",
            email = "applicant2@example.com"
        ),
        ApplicantSummary(
            id = "app3",
            name = "가단",
            gender = "남",
            age = 24,
            department = "커뮤니케이션학과",
            status = "interview_scheduled",
            statusText = "면접 예약 완료",
            applicationDate = "2024-08-14",
            phoneNumber = "010-3456-7890",
            email = "applicant3@example.com",
            interviewDate = "2024-08-25",
            interviewTime = "14:00-14:30"
        ),
        ApplicantSummary(
            id = "app4",
            name = "벨코",
            gender = "남",
            age = 24,
            department = "커뮤니케이션학과",
            status = "interview_completed",
            statusText = "7/4 16:00 면접 완료",
            applicationDate = "2024-08-13",
            phoneNumber = "010-4567-8901",
            email = "applicant4@example.com",
            interviewDate = "2024-07-04",
            interviewTime = "16:00-16:30"
        ),
        ApplicantSummary(
            id = "app5",
            name = "세오",
            gender = "여",
            age = 24,
            department = "커뮤니케이션학과",
            status = "passed",
            statusText = "최종 합격",
            applicationDate = "2024-08-12",
            phoneNumber = "010-5678-9012",
            email = "applicant5@example.com",
            interviewDate = "2024-07-10",
            interviewTime = "15:00-15:30"
        ),
        ApplicantSummary(
            id = "app6",
            name = "도법",
            gender = "남",
            age = 24,
            department = "커뮤니케이션학과",
            status = "failed",
            statusText = "불합격",
            applicationDate = "2024-08-11",
            phoneNumber = "010-6789-0123",
            email = "applicant6@example.com",
            interviewDate = "2024-07-08",
            interviewTime = "13:30-14:00"
        ),
        ApplicantSummary(
            id = "app7",
            name = "백지",
            gender = "남",
            age = 24,
            department = "커뮤니케이션학과",
            status = "failed",
            statusText = "탈락된 결과 공유 완료",
            applicationDate = "2024-08-10",
            phoneNumber = "010-7890-1234",
            email = "applicant7@example.com",
            interviewDate = "2024-07-12",
            interviewTime = "10:00-10:30"
        )
    )
}