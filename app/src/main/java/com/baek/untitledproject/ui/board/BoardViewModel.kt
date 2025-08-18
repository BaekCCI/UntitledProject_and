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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class BoardViewModel @Inject constructor(
    private val boardRepository: BoardRepository
) : ViewModel() {

    init {
        loadBoardList()
    }

    private val _boardList = MutableStateFlow<Result<List<PostSummary>>>(Result.Loading)
    val boardList: StateFlow<Result<List<PostSummary>>> = _boardList

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    fun loadBoardList() {
        viewModelScope.launch {
            _boardList.value = Result.Loading
            val result = boardRepository.getPostSummaryList()
            Log.d("BoardViewModel", "getBoardList 결과 = $result")
            _boardList.value = result
        }
    }

    fun updateQuery(q: String) {
        _query.value = q
    }

    private fun filter(list: List<PostSummary>, q: String) =
        if (q.isBlank()) emptyList()
        else list.filter {
            it.title.contains(q, true) ||
                    it.organization.contains(q, true)
        }

    val filteredList: StateFlow<List<PostSummary>> =
        combine(boardList, query) { state, q ->
            val all = (state as? Result.Success)?.data.orEmpty()
            filter(all, q)               // 검색 중에 쓸 데이터
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

}
