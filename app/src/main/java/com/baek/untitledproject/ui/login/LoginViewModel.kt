package com.baek.untitledproject.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baek.untitledproject.data.local.model.EmailLinkResult
import com.baek.untitledproject.domain.data.User
import com.baek.untitledproject.domain.repository.UserRepository
import com.baek.untitledproject.domain.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private var emailResult: EmailLinkResult? = null

    private var _loginState = MutableStateFlow<Result<User>>(Result.None)
    val loginState: StateFlow<Result<User>> = _loginState

    private var _canLogin = MutableStateFlow<Result<Boolean>>(Result.None)
    val canLogin: StateFlow<Result<Boolean>> = _canLogin

    private var _joinState = MutableStateFlow<Result<User>>(Result.None)
    val joinState: StateFlow<Result<User>> = _joinState

    fun acceptEmailResult(result: EmailLinkResult) {
        emailResult = result
    }

    fun existsUser() {
        viewModelScope.launch {
            _canLogin.value = Result.Loading
            _canLogin.value = userRepository.userExists(emailResult!!.uid)
        }
    }


    fun login() {
        viewModelScope.launch {
            _loginState.value = Result.Loading
            _loginState.value = userRepository.syncUser(emailResult!!.uid)
        }
    }

    fun clearCanLogin() {
        _canLogin.value = Result.None
    }

    fun completeJoin(
        name: String,
        birth: String,
        department: String,
        studentId: String,
        gender: String
    ) {
        if (emailResult == null) {
            _joinState.value = Result.Error()
            return
        }
        viewModelScope.launch {
            _joinState.value = Result.Loading

            val user = User(
                userId = emailResult!!.uid,
                email = emailResult!!.email,
                name = name,
                birthYear = birth.substring(0, 3).toInt(),
                gender = gender,
                department = department,
                studentId = studentId,
                termsAgreed = true,
                privacyAgreed = true
            )
            val result = userRepository.saveUser(emailResult!!.uid, user)
            _joinState.value = result
        }
    }
}