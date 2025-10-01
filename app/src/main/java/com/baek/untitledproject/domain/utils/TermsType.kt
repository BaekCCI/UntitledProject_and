package com.baek.untitledproject.domain.utils

import androidx.annotation.RawRes
import com.baek.untitledproject.R

enum class TermsType(val title: String, @RawRes val resId: Int) {
    SERVICE("서비스 이용약관", R.raw.terms_service),
    PRIVACY("개인정보 처리방침", R.raw.terms_privacy),
    YOUTH_POLICY("청소년 보호 정책", R.raw.terms_youth_policy),
    OPEN_SOURCE("오픈소스 라이선스", R.raw.terms_open_source),
    COMMUNITY_RULES("커뮤니티 이용규칙", R.raw.terms_community_rules)
}
