package com.baek.untitledproject.ui.board.write

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baek.untitledproject.domain.data.Post
import com.baek.untitledproject.domain.repository.BoardRepository
import com.baek.untitledproject.domain.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
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


    //첫번째 화면 진입 시 데이터 불러오기(수정 버튼으로 접근시)
    fun initPostData(postId: String?) {
        if (postId == null) return

        viewModelScope.launch {
            _prevPost.value = Result.Loading
            val result = boardRepository.getPost(postId)
            Log.d("BoardWriteViewModel", "initField: $postId 결과 = $result")
            _prevPost.value = result
            if (result is Result.Success) {
                _editingPost.value = result.data.copy()
                initUiImagesFromPost()
            }
        }
    }


    fun updateRecruitPeriod(startDate: LocalDate, endDate: LocalDate) {
        _editingPost.value = _editingPost.value.copy(
            recruitmentStart = startDate,
            recruitmentEnd = endDate
        )
    }

    //화면용 임시 이미지 리스트(첫번째 화면에서만 활용)
    private val _editingImages = MutableStateFlow<List<Uri>>(emptyList())
    val editingImages: StateFlow<List<Uri>> = _editingImages

    private fun initUiImagesFromPost() {
        _editingImages.value = _editingPost.value.imageUris
    }

    fun addUiImage(uri: Uri) {
        val cur = _editingImages.value
        if (cur.size >= 5) return
        _editingImages.value = cur + uri
    }

    fun removeUiImage(idx: Int) {
        val list = _editingImages.value.toMutableList()
        list.removeAt(idx)
        _editingImages.value = list
    }

    fun updateInfoWrite(
        title: String,
        organization: String,
        content: String
    ) {
        _editingPost.value = _editingPost.value.copy(
            title = title,
            organization = organization,
            content = content,
            imageUris = editingImages.value
        )
    }
}