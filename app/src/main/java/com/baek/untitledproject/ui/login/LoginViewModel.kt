package com.baek.untitledproject.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    private var _loginState = MutableStateFlow<Result<User>>(Result.None)
    val loginState: StateFlow<Result<User>> = _loginState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = Result.Loading
            val result = userRepository.login(email, password)
            _loginState.value = result
        }
    }
}