package com.baek.untitledproject.ui.board.apply

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baek.untitledproject.domain.data.ApplicationRequirements
import com.baek.untitledproject.domain.data.QuestionAnswer
import com.baek.untitledproject.domain.data.User
import com.baek.untitledproject.domain.repository.ApplyRepository
import com.baek.untitledproject.domain.repository.SessionRepository
import com.baek.untitledproject.domain.repository.UserRepository
import com.baek.untitledproject.domain.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ApplicationViewModel @Inject constructor(
    private val applyRepository: ApplyRepository,
    private val userRepository: UserRepository,
    private val sessionRepository: SessionRepository
) : ViewModel() {

    private var _user = MutableStateFlow<Result<User>>(Result.Loading)
    val user: StateFlow<Result<User>> = _user

    private var load = false

    private val _applicationRequirement =
        MutableStateFlow<Result<ApplicationRequirements>>(Result.Loading)
    val applicationRequirement: StateFlow<Result<ApplicationRequirements>> = _applicationRequirement

    private val _answers = MutableStateFlow<List<QuestionAnswer>>(emptyList())
    val answers: StateFlow<List<QuestionAnswer>> = _answers

    private val _submitState = MutableStateFlow<Result<String>>(Result.None)
    val submitState: StateFlow<Result<String>> = _submitState

    init {
        viewModelScope.launch {
            _user.value = Result.Loading
            //userId 가져오기
            val userId = sessionRepository.currentUid()
            if (userId == null) { //로그아웃 상태이면
                _user.value = Result.Error()
                return@launch
            }
            //user정보 가져오기
            val result = userRepository.getUser(userId)
            _user.value = result
        }
    }


    fun load(postId: String) {
        if (load) return
        viewModelScope.launch {
            _applicationRequirement.value = Result.Loading
            val result = applyRepository.getApplicationRequirement(postId)
            Log.d("ApplicationViewModel", "load: $postId 결과 = $result")
            _applicationRequirement.value = result
            if (result is Result.Success) {
                val questions = result.data.customQuestions.map {
                    QuestionAnswer(
                        questionId = it.questionId,
                        questionText = it.questionText
                    )
                }
                _answers.value = questions
            }
            load = true
        }
    }

    fun saveAnswers(inputAnswers: Map<String, String>) { //id, anser
        _answers.update { cur ->
            cur.map { prev ->
                val newAnswer = inputAnswers[prev.questionId]
                if (newAnswer == null || newAnswer == prev.answerText) prev
                else prev.copy(answerText = newAnswer)
            }
        }
    }

    fun submitApplication() {
        if (submitState.value is Result.Loading) return

        viewModelScope.launch {
            _submitState.value = Result.Loading

            val req = applicationRequirement.value
            val user = user.value
            if (req !is Result.Success) {
                _submitState.value = Result.Error("지원 요건을 불러오지 못했습니다.")
                return@launch
            }
            if (user !is Result.Success) {
                _submitState.value = Result.Error(message = "사용자 정보를 불러오지 못했습니다.")
                return@launch
            }
            val result = applyRepository.submitApplication(req.data, user.data, answers.value)

            Log.d("ApplicationViewModel",result.toString())
            _submitState.value = result
        }
    }



}