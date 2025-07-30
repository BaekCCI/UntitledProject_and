package com.baek.untitledproject.ui.board

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baek.untitledproject.domain.data.BoardSummary
import com.baek.untitledproject.domain.repository.BoardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.baek.untitledproject.domain.utils.Result

@HiltViewModel
class BoardViewModel @Inject constructor(
    private val boardRepository: BoardRepository
) : ViewModel() {

    private val _boardList = MutableStateFlow<Result<List<BoardSummary>>>(Result.Loading)
    val boardList: StateFlow<Result<List<BoardSummary>>> = _boardList

    fun loadBoardList() {
        viewModelScope.launch {
            _boardList.value = Result.Loading
            val result = boardRepository.getBoardList()
            Log.d("BoardViewModel", "getBoardList 결과 = $result")
            _boardList.value = result
        }
    }

}