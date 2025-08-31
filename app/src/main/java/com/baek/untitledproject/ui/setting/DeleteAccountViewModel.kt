package com.baek.untitledproject.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baek.untitledproject.domain.data.User
import com.baek.untitledproject.domain.repository.AuthRepository
import com.baek.untitledproject.domain.repository.UserRepository
import com.baek.untitledproject.domain.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeleteAccountViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private var _reAuthState = MutableStateFlow<Result<String>>(Result.None)
    val reAuthState: StateFlow<Result<String>> = _reAuthState

    private var lastPw: String? = null

    private var _deleteState = MutableStateFlow<Result<Unit>>(Result.None)
    val deleteState: StateFlow<Result<Unit>> = _deleteState

    fun verifyPassword(password: String) {
        viewModelScope.launch {
            _reAuthState.value = Result.Loading
            val result = authRepository.reAuthenticate(password)
            _reAuthState.value = result
            lastPw = password
        }
    }

    fun onPasswordInputChanged(curPw: String) {

        if (!lastPw.isNullOrBlank() && !curPw.equals(lastPw, ignoreCase = true)) {
            if (_reAuthState.value !is Result.None) {
                _reAuthState.value = Result.None
            }
        }
    }

    fun deleteAccount(userId: String) {
        viewModelScope.launch {
            _deleteState.value = Result.Loading
            val result = authRepository.deleteAccount()
            if (result is Result.Success) {
                userRepository.deleteUser(userId)
            }
            _deleteState.value = result
        }
    }
}