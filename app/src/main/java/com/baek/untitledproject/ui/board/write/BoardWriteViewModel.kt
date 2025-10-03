package com.baek.untitledproject.ui.board.write

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.baek.untitledproject.domain.data.TimeSlot
import com.baek.untitledproject.domain.data.User
import com.baek.untitledproject.domain.repository.BoardRepository
import com.baek.untitledproject.domain.repository.SessionRepository
import com.baek.untitledproject.domain.repository.UserRepository
import com.baek.untitledproject.domain.utils.Result
import com.baek.untitledproject.ui.board.write.common.BaseWriteViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class BoardWriteViewModel @Inject constructor(
    private val boardRepository: BoardRepository,
    private val sessionRepository: SessionRepository,
    private val userRepository: UserRepository
) : BaseWriteViewModel(sessionRepository, userRepository) {


    //두번째
    fun updateHasInterview(hasInterview: Boolean) {
        _post.update { cur ->
            cur.copy(hasInterview = hasInterview)
        }
    }

    fun updateInterviewLocation(loc: String) {
        _post.value = post.value.copy(
            interviewLocation = loc
        )
    }

    fun updateInterviewSlot(slot: Map<LocalDate, List<TimeSlot>>, capacity: Int, step: Int) {
        _post.update { cur ->
            cur.copy(
                interviewSlot = slot,
                maxCapacity = capacity,
                interviewSlotStep = step
            )
        }
    }

    //세번째
    fun updateRequirements(
        name: Boolean = false,
        gender: Boolean = false,
        age: Boolean = false,
        dept: Boolean = false,
        studentId: Boolean = false,
    ) {
        _post.value = post.value.copy(
            requiresName = name,
            requiresGender = gender,
            requiresAge = age,
            requiresDepartment = dept,
            requiresStudentId = studentId,
        )
    }

    private val _customQuestions = MutableStateFlow<List<String>>(emptyList())
    val customQuestions: StateFlow<List<String>> = _customQuestions

    fun updateQuestion(questions: List<String>) {
        _customQuestions.value = questions
    }

    //작성 완료

    private val _submitResult = MutableStateFlow<Result<String>>(Result.None)
    val submitResult: StateFlow<Result<String>> = _submitResult

    fun completePost() {
        if (_submitResult.value is Result.Loading) return

        _post.value = post.value.copy(
            imageUris = images.value,
            customQuestions = customQuestions.value
        )
        viewModelScope.launch {
            val currentUser = (user.value as? Result.Success<User>)?.data
                ?: run {
                    _submitResult.value = Result.Error("유저 정보가 없습니다.")
                    return@launch
                }
            _submitResult.value = Result.Loading
//            val result = boardRepository.submitPost(post.value, currentUser)
//            _submitResult.value = result
            _submitResult.value = Result.Success("asd")
           // Log.d("BoardWriteViewModel", "completePost 결과: $result")
        }
    }

}