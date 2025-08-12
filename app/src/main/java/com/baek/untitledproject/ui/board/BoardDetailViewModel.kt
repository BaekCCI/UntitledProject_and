package com.baek.untitledproject.ui.board

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baek.untitledproject.domain.data.Post
import com.baek.untitledproject.domain.repository.BoardRepository
import com.baek.untitledproject.domain.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BoardDetailViewModel @Inject constructor(
    private val boardRepository: BoardRepository
) : ViewModel() {

    private val _board = MutableStateFlow<Result<Post>>(Result.Loading)
    val board: StateFlow<Result<Post>> = _board

    fun loadBoardData(id: String) {
        viewModelScope.launch {
            _board.value = Result.Loading
            val result = boardRepository.getPost(id)
            Log.d("BoardDetailViewModel", "getBoard: $id 결과 = $result")
            _board.value = result
        }
    }
}