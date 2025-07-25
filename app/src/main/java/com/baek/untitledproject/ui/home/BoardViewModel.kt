package com.baek.untitledproject.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baek.untitledproject.domain.data.BoardSummary
import com.baek.untitledproject.domain.repository.BoardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BoardViewModel @Inject constructor(
    private val boardRepository: BoardRepository
) : ViewModel() {

    private val _boardList = MutableStateFlow<List<BoardSummary>>(emptyList())
    val boardList: StateFlow<List<BoardSummary>> = _boardList

    fun loadBoardList() {
        viewModelScope.launch {
            _boardList.value = boardRepository.getBoardList()
        }
    }

}