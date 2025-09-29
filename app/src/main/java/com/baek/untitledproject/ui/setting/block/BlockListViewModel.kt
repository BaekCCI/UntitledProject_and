package com.baek.untitledproject.ui.setting.block

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baek.untitledproject.domain.data.Block
import com.baek.untitledproject.domain.repository.SessionRepository
import com.baek.untitledproject.domain.repository.UserRepository
import com.baek.untitledproject.domain.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BlockListViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _blockList = MutableStateFlow<Result<List<Block>>>(Result.None)
    val blockList: StateFlow<Result<List<Block>>> = _blockList

    private val _unblockEvents = MutableSharedFlow<Result<Unit>>(replay = 0)
    val unblockEvents: SharedFlow<Result<Unit>> = _unblockEvents

    init {
        getBlockList()
    }

    fun getBlockList() {
        viewModelScope.launch {
            _blockList.value = Result.Loading
            val userId = sessionRepository.currentUid()
            if (userId == null) {
                _blockList.value = Result.Error("로그인 후 이용해주세요.")
                return@launch
            }

            val result = userRepository.getBlockedUsers(userId)
            _blockList.value = result
            Log.d("BlockListViewModel", result.toString())

        }
    }

    fun unBlockUser(targetId: String) {
        viewModelScope.launch {
            val userId = sessionRepository.currentUid() ?: return@launch
            val result = userRepository.unBlockUser(userId, targetId)
            _unblockEvents.emit(result)
        }
    }

}