package com.baek.untitledproject.ui.board

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baek.untitledproject.domain.data.PostSummary
import com.baek.untitledproject.domain.repository.BoardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import com.baek.untitledproject.domain.utils.Result
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val boardRepository: BoardRepository
) : ViewModel() {

    private val _searchedBoards = MutableStateFlow<Result<List<PostSummary>>>(Result.None)
    val searchedBoards: StateFlow<Result<List<PostSummary>>> = _searchedBoards

    fun searchBoard(keyword: String) {
        viewModelScope.launch {
            _searchedBoards.value = Result.Loading
            val result = boardRepository.searchBoard(keyword)
            Log.d("SearchViewModel", "searchBoard 결과 = $result")
            _searchedBoards.value = result
        }
    }

}