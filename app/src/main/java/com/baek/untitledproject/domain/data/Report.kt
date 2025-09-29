package com.baek.untitledproject.domain.data

import java.time.LocalDate

data class Report(
    val reportId: String = "",
    val reporterUserId: String = "",
    val reportedUserId: String = "",
    val postId: String? = null,
    val conversationId: String? = null,
    val messageId: String? = null,
    val reportType: String = "other",
    val reason: String? = null,
    val status: String = "pending",
    val createdAt: LocalDate? = null
)