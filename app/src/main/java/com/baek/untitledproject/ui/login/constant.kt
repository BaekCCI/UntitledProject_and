package com.baek.untitledproject.ui.login

enum class AuthEntry() {
    JOIN, LOGIN;

    companion object {
        fun String?.toEntry(): AuthEntry? {
            return when (this?.uppercase()) {
                "JOIN" -> JOIN
                "LOGIN" -> LOGIN
                else -> null
            }
        }
    }
}

const val EMAIL_DOMAIN = "@jbnu.ac.kr"