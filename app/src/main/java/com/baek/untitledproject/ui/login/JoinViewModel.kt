package com.baek.untitledproject.ui.login

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baek.untitledproject.data.local.model.EmailLinkResult
import com.baek.untitledproject.domain.data.User
import com.baek.untitledproject.domain.repository.AuthRepository
import com.baek.untitledproject.domain.repository.EmailVerifyRepository
import com.baek.untitledproject.domain.repository.UserRepository
import com.baek.untitledproject.domain.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JoinViewModel @Inject constructor(
    private val emailVerifyRepository: EmailVerifyRepository,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    //이메일 전송 상태
    private val _sendState = MutableStateFlow<Result<Unit>>(Result.None)
    val sendState: StateFlow<Result<Unit>> = _sendState

    var lastRequestedEmail: String? = null

    val emailCacheFlow = emailVerifyRepository.emailFlow
    init {
        viewModelScope.launch {
            emailVerifyRepository.getEmail()?.let { email ->
                lastRequestedEmail = email
            }
        }
    }

    //인증 상태
    private val _signInState = MutableStateFlow<Result<EmailLinkResult>>(Result.None)
    val signInState: StateFlow<Result<EmailLinkResult>> = _signInState

    //이메일 전송
    fun requestEmailLink(email: String) {
        viewModelScope.launch {
            _sendState.value = Result.Loading
            val result = emailVerifyRepository.sendSignInLink(email)
            if (result is Result.Success) {
                lastRequestedEmail = email
            }
            _sendState.value = result
            Log.d("EmailVerifyViewModel", "$email : ${result}")
        }
    }

    fun onEmailInputChanged(currentEmail: String) {
        // 이전에 보낸 이메일이 있고, 현재 입력이 다르면 전송 상태 초기화
        if (!lastRequestedEmail.isNullOrBlank() &&
            !currentEmail.equals(lastRequestedEmail, ignoreCase = true)
        ) {
            if (_sendState.value is Result.Success) {
                _sendState.value = Result.None
            }
        }
    }

    //딥링크 처리
    fun handleDeepLink(uri: Uri) = viewModelScope.launch {
        _signInState.value = Result.Loading
        val result = emailVerifyRepository.handleDeepLink(lastRequestedEmail, uri)
        if (result is Result.Success) {
            if (result.data.isNewUser) {
                _signInState.value = result
            } else {
                val isNew = isNewUser(result.data.uid)
                _signInState.value = Result.Success(result.data.copy(isNewUser = isNew))
            }
        } else {
            _signInState.value = result
        }
        Log.d("EmailVerifyViewModel", result.toString())
    }

    //존재하는 유저인지 확인
    private suspend fun isNewUser(userId: String): Boolean {
        val result = userRepository.userExists(userId)
        if (result is Result.Success) {
            return !result.data
        }
        return false
    }

    //--- 비밀번호 설정 ---

    private var _setPwState = MutableStateFlow<Result<Unit>>(Result.None)
    val setPwState: StateFlow<Result<Unit>> = _setPwState

    fun setPassword(password: String) {
        viewModelScope.launch {
            _setPwState.value = Result.Loading
            _setPwState.value = authRepository.setPassword(password)
        }
    }

    //--- 정보 입력 ---
    private var _joinState = MutableStateFlow<Result<User>>(Result.None)
    val joinState: StateFlow<Result<User>> = _joinState

    fun completeJoin(
        name: String,
        birth: String,
        department: String,
        studentId: String,
        gender: String
    ) {
        val emailResult = signInState.value

        if (emailResult !is Result.Success) {
            _joinState.value = Result.Error()
            return
        }

        viewModelScope.launch {
            _joinState.value = Result.Loading

            val user = User(
                userId = emailResult.data.uid,
                email = emailResult.data.email,
                name = name,
                birthYear = birth.substring(0, 4).toInt(),
                gender = gender,
                department = department,
                studentId = studentId,
                termsAgreed = true,
                privacyAgreed = true
            )
            val result = userRepository.saveUser(emailResult.data.uid, user)
            _joinState.value = result
        }
    }
}