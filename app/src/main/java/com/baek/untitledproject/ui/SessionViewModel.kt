package com.baek.untitledproject.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baek.untitledproject.domain.repository.SessionRepository
import com.baek.untitledproject.domain.repository.UserRepository
import com.baek.untitledproject.domain.utils.SessionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    sessionRepository: SessionRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    val session: StateFlow<SessionState> = sessionRepository.session
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SessionState.LoggedOut)

    private var lastUserId: String? = null

    init {
        viewModelScope.launch {
            session.collect { state ->
                when (state) {
                    //로그인 상태이면
                    is SessionState.LoggedIn -> {
                        //이전 id(null)와 로그인된 id가 다르면
                        if (lastUserId != state.userId) {
                            //서버->로컬 데이터 동기화
                            launch { userRepository.syncUser(state.userId) }
                            lastUserId = state.userId
                        }
                    }

                    //로그아웃 상태이면
                    is SessionState.LoggedOut -> {
                        //lastUserId가 남아있다면
                        if (lastUserId != null) {
                            //로컬 데이터 정리 및 lastUserId=null
                            launch { userRepository.clearLocal() }
                            lastUserId = null
                        }
                    }
                }
            }

        }
    }
}