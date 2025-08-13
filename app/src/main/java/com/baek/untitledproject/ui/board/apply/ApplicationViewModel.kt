package com.baek.untitledproject.ui.board.apply

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baek.untitledproject.domain.data.ApplicationRequirements
import com.baek.untitledproject.domain.data.QuestionAnswer
import com.baek.untitledproject.domain.repository.ApplyRepository
import com.baek.untitledproject.domain.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ApplicationViewModel @Inject constructor(
    private val repository: ApplyRepository
) : ViewModel() {

    private val _applicationRequirement =
        MutableStateFlow<Result<ApplicationRequirements>>(Result.Loading)
    val applicationRequirement: StateFlow<Result<ApplicationRequirements>> = _applicationRequirement

    private val _answers = MutableStateFlow<Map<String, String>>(emptyMap())
    val answers: StateFlow<Map<String, String>> = _answers

    fun load(postId: String) {
        viewModelScope.launch {
            _applicationRequirement.value = Result.Loading
            val result = repository.getApplicationRequirement(postId)
            Log.d("ApplicationViewModel", "load: $postId 결과 = $result")
            _applicationRequirement.value = result

        }
    }


    fun saveAnswers(answers: Map<String,String>) {

    }


}