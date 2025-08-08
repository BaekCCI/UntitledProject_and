package com.baek.untitledproject.ui.board.write

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
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class BoardWriteViewModel @Inject constructor(
    private val boardRepository: BoardRepository
) : ViewModel() {
    private val _prevPost = MutableStateFlow<Result<Post>>(Result.None)
    val prevPost: StateFlow<Result<Post>> = _prevPost

    private val _editingPost = MutableStateFlow(Post())
    val editingPost: StateFlow<Post> = _editingPost

    fun initField(postId: String?) {
        if (postId == null) return

        viewModelScope.launch {
            _prevPost.value = Result.Loading
            val result = boardRepository.getPost(postId)
            Log.d("BoardWriteViewModel", "initField: $postId 결과 = $result")
            _prevPost.value = result
            if (result is Result.Success) {
                _editingPost.value = result.data.copy()
            }
        }
    }
    fun updateRecruitPeriod(startDate:LocalDate, endDate: LocalDate){
        _editingPost.value = _editingPost.value.copy(
            recruitmentStart = startDate,
            recruitmentEnd = endDate
        )
    }
}
/*
@HiltViewModel
class BoardWriteViewModel @Inject constructor(
    private val repository: BoardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BoardWriteUiState())
    val uiState: StateFlow<BoardWriteUiState> = _uiState.asStateFlow()

    private val _loadState = MutableStateFlow<Result<Unit>>(Result.Idle)
    val loadState: StateFlow<Result<Unit>> = _loadState.asStateFlow()

//    private var loadedPostId: String? = null

    fun loadPostIfNeeded(postId: String?) {
        if (postId == null || postId == loadedPostId) return

        viewModelScope.launch {
            _loadState.value = Result.Loading
            try {
                val board = repository.getPostById(postId)
                val slots = repository.getInterviewSlotsByPostId(postId)

                if (board == null) {
                    _loadState.value = Result.Error("게시글을 찾을 수 없습니다.")
                    return@launch
                }

                _uiState.value = board.toWriteUiState(slots)
                loadedPostId = postId
                _loadState.value = Result.Success(Unit)

            } catch (e: Exception) {
                _loadState.value = Result.Error("불러오는 중 오류 발생: ${e.message}")
            }
        }
    }

    // 예시: 이미지 추가
    fun addImage(uri: Uri) {
        _uiState.update { it.copy(imageUris = it.imageUris + uri) }
    }

    // 예시: 면접 시간 추가
    fun addInterviewTime(date: LocalDate, time: String) {
        val current = _uiState.value.interviewTimeMap[date] ?: emptyList()
        if (time !in current) {
            _uiState.update {
                it.copy(interviewTimeMap = it.interviewTimeMap + (date to (current + time)))
            }
        }
    }

    fun removeInterviewTime(date: LocalDate, time: String) {
        val current = _uiState.value.interviewTimeMap[date]?.toMutableList() ?: return
        current.remove(time)
        val newMap = if (current.isEmpty()) {
            _uiState.value.interviewTimeMap - date
        } else {
            _uiState.value.interviewTimeMap + (date to current)
        }
        _uiState.update { it.copy(interviewTimeMap = newMap) }
    }
}

 */