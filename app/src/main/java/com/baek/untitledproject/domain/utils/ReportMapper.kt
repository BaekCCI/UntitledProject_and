package com.baek.untitledproject.domain.utils


enum class ReportTopic { POST, CONVERSATION, MESSAGE }

enum class ReportType(val code: String, val uiText: String) {
    INSULT("insult", "욕설 및 비하"),
    HATE_SPEECH("hate_speech", "폭력 및 혐오표현"),
    SEXUAL("sexual_content", "음란물 및 성적인 표현"),
    GAMBLING("gambling", "도박 및 사행성 조장"),
    SPAM("spam_ads", "스팸 및 광고"),
    PERSONAL("personal_info_leak", "개인정보 유포"),
    IMPERSONATION("impersonation_or_false", "사칭 및 허위 정보"),
    OTHER("other", "기타");

    companion object {
        fun fromCode(code: String): ReportType =
            entries.find { it.code == code } ?: OTHER
    }
}