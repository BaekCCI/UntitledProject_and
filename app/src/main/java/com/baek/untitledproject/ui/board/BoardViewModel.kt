package com.baek.untitledproject.ui.board

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baek.untitledproject.domain.data.PostSummary
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

    private val _boardList = MutableStateFlow<Result<List<PostSummary>>>(Result.Loading)
    val boardList: StateFlow<Result<List<PostSummary>>> = _boardList

    fun loadBoardList() {
        viewModelScope.launch {
            _boardList.value = Result.Loading
            val result = boardRepository.getPostSummaryList()
            Log.d("BoardViewModel", "getBoardList 결과 = $result")
            _boardList.value = result
        }
    }

}