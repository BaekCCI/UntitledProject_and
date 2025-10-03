package com.baek.untitledproject.ui.board.write

import android.net.Uri
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.baek.untitledproject.domain.data.PostWrite
import com.baek.untitledproject.domain.repository.BoardRepository
import com.baek.untitledproject.domain.repository.SessionRepository
import com.baek.untitledproject.domain.repository.UserRepository
import com.baek.untitledproject.domain.utils.Result
import com.baek.untitledproject.domain.utils.toLocalDate
import com.baek.untitledproject.domain.utils.toLong
import com.baek.untitledproject.ui.board.write.common.BaseWriteViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditPostViewModel @Inject constructor(
    private val boardRepository: BoardRepository,
    private val sessionRepository: SessionRepository,
    private val userRepository: UserRepository
) : BaseWriteViewModel(sessionRepository, userRepository) {

    private val _postState = MutableStateFlow<Result<PostWrite>>(Result.None)
    val postState: StateFlow<Result<PostWrite>> = _postState

    private val _submitState = MutableStateFlow<Result<String>>(Result.None)
    val submitState : StateFlow<Result<String>> = _submitState

    private var isImageChanged = false

    fun init(postId:String) {
        viewModelScope.launch {
            _postState.value = Result.Loading
            val result = boardRepository.getPostForEdit(postId)
            if (result is Result.Success) {
                _post.value = result.data
                _images.value = result.data.imageUris
                result.data.recruitmentEnd?.let { endMillis = it.toLong() }
            }
            Log.d("EditPostViewModel",result.toString())
            _postState.value = result
        }
    }

    override fun addImage(uri: Uri) {
        super.addImage(uri)
        if(!isImageChanged){
            _post.update { cur ->
                cur.copy(
                    isImageChanged = true
                )
            }
            isImageChanged = true
        }
    }

    override fun removeImage(idx: Int) {
        super.removeImage(idx)
        if(!isImageChanged){
            _post.update { cur ->
                cur.copy(
                    isImageChanged = true
                )
            }
            isImageChanged = true
        }
    }

    fun submit(
        title: String,
        organization: String,
        content: String,
        interviewLoc: String? = null
    ) {
        if(submitState.value is Result.Loading) return
        _post.update { cur ->
            cur.copy(
                title = title,
                organization = organization,
                interviewLocation = if (cur.hasInterview == true) interviewLoc else cur.interviewLocation,
                recruitmentEnd = endMillis?.toLocalDate(),
                content = content,
                imageUris = images.value
            )
        }
        viewModelScope.launch {
            _submitState.value = Result.Loading
            val result = boardRepository.editPost(post.value)
            _submitState.value = result
        }
    }
}