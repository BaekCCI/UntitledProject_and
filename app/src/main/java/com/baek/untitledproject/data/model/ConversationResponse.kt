package com.baek.untitledproject.data.model

import com.google.firebase.Timestamp

data class ConversationResponse(
    val conversation_id: String = "",
    val participant1_user_id: String = "",
    val participant2_user_id: String = "",
    val post_id: String = "",

    // 공고 정보
    val post_title: String = "",
    val post_organization: String = "",

    // 참여자 정보
    val participant1_name: String = "",
    val participant1_role: String = "", // "recruiter" or "applicant"
    val participant2_name: String = "",
    val participant2_role: String = "", // "recruiter" or "applicant"

    val created_at: Timestamp? = null,
    val updated_at: Timestamp? = null
)

data class MessageResponse(
    val message_id: String = "",
    val sender_user_id: String = "",
    val content: String = "",
    val is_read: Boolean = false,
    val created_at: Timestamp? = null
)