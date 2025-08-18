package com.baek.untitledproject.domain.data

data class ApplicantSummary(
    val id: String,                          // application_id
    val name: String,                        // applicant_name
    val gender: String,                      // applicant_gender
    val age: Int,                           // 계산된 값 (현재년도 - applicant_birth_year)
    val department: String?,                 // applicant_department
    val studentId: String?,                  // applicant_student_id
    val phoneNumber: String?,                // applicant_phone
    val status: String,                      // status
    val isPassed: Boolean?,                  // is_passed
    val isNotified: Boolean = false,         // 알림 발송 여부 (계산된 값)

    // UI 표시용 변환된 값들
    val statusText: String,                  // 상태 텍스트 (한글)
    val customQuestionAnswers: List<QuestionAnswer> = emptyList() // 커스텀 질문/답변
)