package com.baek.untitledproject.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baek.untitledproject.domain.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FindAccountViewModel @Inject constructor(

) : ViewModel() {

    private val _sendState = MutableStateFlow<Result<String>>(Result.None)
    val sendState: StateFlow<Result<String>> = _sendState

    fun resetSendState() {
        if (_sendState.value is Result.None) return
        _sendState.value = Result.None
    }

    fun sendEmail(email: String) {
        viewModelScope.launch {

            _sendState.value = Result.Loading
            delay(3000)
            _sendState.value = Result.Success(email)
            delay(3000)
            _sendState.value = Result.Error("존재하지 않는 이메일이에요")
        }
    }

}