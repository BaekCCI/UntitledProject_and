package com.baek.untitledproject.ui.board

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baek.untitledproject.domain.data.Board
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

    private val _board = MutableStateFlow<Result<Board>>(Result.Loading)
    val board: StateFlow<Result<Board>> = _board

    fun loadBoardData(id: String) {
        viewModelScope.launch {
            _board.value = Result.Loading
            val result = boardRepository.getBoard(id)
            Log.d("BoardDetailViewModel", "getBoard: $id 결과 = $result")
            _board.value = result
        }
    }
}