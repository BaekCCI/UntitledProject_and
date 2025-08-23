package com.baek.untitledproject.ui.login

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baek.untitledproject.data.local.model.AuthCache
import com.baek.untitledproject.data.local.model.EmailLinkResult
import com.baek.untitledproject.domain.repository.EmailVerifyRepository
import com.baek.untitledproject.domain.utils.Result
import com.google.rpc.context.AttributeContext.Auth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class EmailVerifyViewModel @Inject constructor(
    private val emailVerifyRepository: EmailVerifyRepository
) : ViewModel() {

    //이메일 전송 상태
    private val _sendState = MutableStateFlow<Result<Unit>>(Result.None)
    val sendState: StateFlow<Result<Unit>> = _sendState

    //링크 인증 상태: true -> 신규, false -> 기존
    private val _signInState = MutableStateFlow<Result<EmailLinkResult>>(Result.None)
    val signInState: StateFlow<Result<EmailLinkResult>> = _signInState

    private val _authCache = MutableStateFlow<Result<AuthCache>>(Result.None)
    val authCache: StateFlow<Result<AuthCache>> = _authCache


    init {
        loadAuthCache()
    }

    //이메일 전송
    fun requestEmailLink(email: String, entry: AuthEntry) {
        viewModelScope.launch {
            _sendState.value = Result.Loading
            val result = emailVerifyRepository.sendSignInLink(email, entry)
            if (result is Result.Success) {
                loadAuthCache()
            }
            _sendState.value = result
            Log.d("EmailVerifyViewModel", "$email : ${result}")
        }
    }

    //딥링크 처리
    fun handleDeepLink(uri: Uri) = viewModelScope.launch {
        _signInState.value = Result.Loading
        val result = emailVerifyRepository.handleDeepLink(uri)
        _signInState.value = result
        Log.d("EmailVerifyViewModel", result.toString())
    }

    //AuthCache 가져오기
    fun loadAuthCache() {
        viewModelScope.launch {
            _authCache.value = Result.Loading
            _authCache.value = emailVerifyRepository.getAuthCache()
        }
    }

    fun clearAuthCache() {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(NonCancellable) {
                emailVerifyRepository.clearAuthCache()
                loadAuthCache()
                Log.d("EmailVerifyViewModel", "Clear Cache")
            }
        }
    }


}