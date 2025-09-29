package com.baek.untitledproject.data.model.mapper

import com.baek.untitledproject.common.utils.toLocalDate
import com.baek.untitledproject.common.utils.toTimestamp
import com.baek.untitledproject.data.model.ReportResponse
import com.baek.untitledproject.domain.data.Report
import java.time.LocalDate


fun Report.toResponse(id: String, now: LocalDate): ReportResponse {
    return ReportResponse(
        report_id = id,
        reporter_user_id = reporterUserId,
        reported_user_id = reportedUserId,
        post_id = postId,
        conversation_id = conversationId,
        message_id = messageId,
        report_type = reportType,
        reason = reason,
        status = "pending",
        created_at = now.toTimestamp()
    )
}

fun ReportResponse.toDomain(): Report {
    return Report(
        reportId = report_id,
        reporterUserId = reporter_user_id,
        reportedUserId = reported_user_id,
        postId = post_id,
        conversationId = conversation_id,
        messageId = message_id,
        reportType = report_type,
        reason = reason,
        status = status,
        createdAt = created_at?.toLocalDate()
    )
}
