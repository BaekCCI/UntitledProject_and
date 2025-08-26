package com.baek.untitledproject.data.remote

import android.net.Uri
import com.baek.untitledproject.data.local.model.EmailLinkResult
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

object AuthRemote {

    suspend fun sendSignInLink(email: String, androidPackageName: String) {
        val settings = ActionCodeSettings.newBuilder()
            .setUrl("https://muje-a649a.web.app/index.html")
            .setHandleCodeInApp(true)
            .setAndroidPackageName(androidPackageName, true, null)
            .build()

        FirebaseAuth.getInstance().sendSignInLinkToEmail(email, settings).await()
    }

    suspend fun handleEmailSignInLink(deepLink: Uri, inputEmail: String): EmailLinkResult {
        // 커스텀 스킴/호스트 확인
        require(deepLink.scheme == "muje" && deepLink.host == "email-verified") {
            "유효하지 않은 딥링크"
        }

        //쿼리 파라미터
        val oobCode = deepLink.getQueryParameter("oobCode")
        val mode = deepLink.getQueryParameter("mode")
        val apiKey = deepLink.getQueryParameter("apiKey")
        val continueUrl = deepLink.getQueryParameter("continueUrl")

        require(!oobCode.isNullOrBlank() && mode == "signIn" && !apiKey.isNullOrBlank()) {
            "파라미터 누락"
        }

        //URL 빌드
        val signInLink = Uri.Builder()
            .scheme("https")
            .authority("muje-a649a.web.app")
            .path("/")
            .appendQueryParameter("oobCode", oobCode)
            .appendQueryParameter("mode", "signIn")
            .appendQueryParameter("apiKey", apiKey)
            .apply {
                if (!continueUrl.isNullOrBlank()) {
                    appendQueryParameter("continueUrl", continueUrl)
                }
            }
            .build()
            .toString()

        val auth = FirebaseAuth.getInstance()
        require(auth.isSignInWithEmailLink(signInLink)) { "유효하지 않은 가입링크" }

        //이메일 로그인
        val result = auth.signInWithEmailLink(inputEmail, signInLink).await()
        val user = requireNotNull(result.user) { "로그인 성공 응답이지만 user=null" }
        return EmailLinkResult(
            uid = user.uid,
            email = user.email ?: inputEmail,
            isNewUser = result.additionalUserInfo?.isNewUser == true
        )
    }

}