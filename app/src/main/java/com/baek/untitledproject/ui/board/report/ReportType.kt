package com.baek.untitledproject.ui.board.report

object ReportType {
    const val INSULT = "insult"
    const val HATE_SPEECH = "hate_speech"
    const val SEXUAL = "sexual_content"
    const val GAMBLING = "gambling"
    const val SPAM = "spam_ads"
    const val PERSONAL = "personal_info_leak"
    const val IMPERSONATION = "impersonation_or_false"
    const val OTHER = "other"
}

enum class ReportTopic { POST, CONVERSATION, MESSAGE }