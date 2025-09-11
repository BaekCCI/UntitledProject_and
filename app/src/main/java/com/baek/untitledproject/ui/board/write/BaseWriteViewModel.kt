package com.baek.untitledproject.ui.board.write

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baek.untitledproject.domain.data.PostWrite
import com.baek.untitledproject.domain.data.User
import com.baek.untitledproject.domain.repository.SessionRepository
import com.baek.untitledproject.domain.repository.UserRepository
import com.baek.untitledproject.domain.utils.Result
import com.baek.untitledproject.domain.utils.toLocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

abstract class BaseWriteViewModel(
    private val sessionRepository: SessionRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    protected val _user = MutableStateFlow<Result<User>>(Result.Loading)
    val user: StateFlow<Result<User>> = _user

    protected val _post = MutableStateFlow(PostWrite())
    val post: StateFlow<PostWrite> = _post

    var endMillis: Long? = null

    init {
        viewModelScope.launch {
            _user.value = Result.Loading
            //userId 가져오기
            val userId = sessionRepository.currentUid()
            if (userId == null) { //로그아웃 상태이면
                _user.value = Result.Error("로그인이 필요합니다.")
                return@launch
            }
            //user정보 가져오기
            val result = userRepository.getUser(userId)
            _user.value = result
        }
    }

    protected val _images = MutableStateFlow<List<Uri>>(emptyList())
    val images: StateFlow<List<Uri>> = _images

    open fun addImage(uri: Uri) {
        val cur = _images.value
        if (cur.size >= 5) return
        _images.value = cur + uri
    }

    open fun removeImage(idx: Int) {
        val list = _images.value.toMutableList()
        list.removeAt(idx)
        _images.value = list
    }

    fun updateInfo(
        title: String,
        organization: String,
        content: String
    ) {
        _post.value = post.value.copy(
            title = title,
            organization = organization,
            recruitmentStart = LocalDate.now(),
            recruitmentEnd = endMillis?.toLocalDate(),
            content = content
        )
    }
}