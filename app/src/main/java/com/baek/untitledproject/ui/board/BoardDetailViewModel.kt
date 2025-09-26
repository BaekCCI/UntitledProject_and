package com.baek.untitledproject.ui.board

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baek.untitledproject.domain.data.Post
import com.baek.untitledproject.domain.data.PostRead
import com.baek.untitledproject.domain.repository.BoardRepository
import com.baek.untitledproject.domain.repository.SessionRepository
import com.baek.untitledproject.domain.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BoardDetailViewModel @Inject constructor(
    private val boardRepository: BoardRepository,
    private val sessionRepository: SessionRepository
) : ViewModel() {

    private val _board = MutableStateFlow<Result<PostRead>>(Result.Loading)
    val board: StateFlow<Result<PostRead>> = _board

    private val userId = sessionRepository.currentUid()

    private val _deleteState = MutableStateFlow<Result<Unit>>(Result.None)
    val deleteState: StateFlow<Result<Unit>> = _deleteState

    var authorId:String =""

    fun loadBoardData(id: String) {
        viewModelScope.launch {
            _board.value = Result.Loading
            val result = boardRepository.getPostForRead(id, userId)
            Log.d("BoardDetailViewModel", "getBoard: $id 결과 = $result")
            if(result is Result.Success){
                authorId = result.data.authorUserId
            }
            _board.value = result
        }
    }

    fun deletePost(id: String) {
        viewModelScope.launch {
            _deleteState.value = Result.Loading
            val result = boardRepository.deletePost(id)
            Log.d("BoardDetailViewModel", "deletePost 결과: $result")
            _deleteState.value = result
        }
    }

}