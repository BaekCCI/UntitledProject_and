package com.baek.untitledproject.data.sample

import com.baek.untitledproject.data.model.NotificationResponse
import com.google.firebase.Timestamp
import java.util.Date

object NotificationSampleData {

    private fun minutesAgo(m: Int) = Timestamp(Date(System.currentTimeMillis() - m * 60_000L))
    private fun hoursAgo(h: Int) = Timestamp(Date(System.currentTimeMillis() - h * 60L * 60_000L))
    private fun daysAgo(d: Int) =
        Timestamp(Date(System.currentTimeMillis() - d * 24L * 60L * 60_000L))

    val notiList = listOf(
        NotificationResponse(
            notification_id = "noti_abc123",
            sender_user_id = "user_abc123",
            receiver_user_id = "user1",
            post_id = "post_abc123",
            sender_organization = "코딩클럽",
            title = "면접 제안",
            message = "면접 일정이 확정되었습니다",
            notification_type = "interview_proposal",
            is_read = false,
            created_at = minutesAgo(30)
        ),
        NotificationResponse(
            notification_id = "noti_def456",
            sender_user_id = "user1", // 시스템 알림
            receiver_user_id = "user1",
            post_id = "post_abc123",
            sender_organization = "시스템",
            title = "모집 마감 임박",
            message = "모집이 내일 마감됩니다",
            notification_type = "deadline_warning",
            is_read = true,
            created_at = hoursAgo(2)
        ),
        NotificationResponse(
            notification_id = "noti_xyz789",
            sender_user_id = "user_xyz789",
            receiver_user_id = "user1",
            post_id = null,
            sender_organization = "코딩클럽",
            title = "새 소식",
            message = "지원서가 접수되었습니다",
            notification_type = "application_received",
            is_read = false,
            created_at = daysAgo(3)
        ),
        NotificationResponse(
            notification_id = "noti_xyz789",
            sender_user_id = "user_xyz789",
            receiver_user_id = "user2",
            post_id = null,
            sender_organization = "코딩클럽",
            title = "새 소식",
            message = "지원서가 접수되었습니다",
            notification_type = "application_received",
            is_read = false,
            created_at = daysAgo(3)
        )
    )

}