package com.baek.untitledproject.domain.data

data class QuestionAnswer(
    val answerId: String = "",       // answer_id
    val applicationId: String = "",  // application_id
    val questionId: String = "",     // question_id
    val questionText: String = "",   // question_text
    val answerText: String? = null,     // answer_text
    var isExpanded: Boolean = true,
    val createdAt: String = ""       // created_at (Timestamp → String)
)