package com.baek.untitledproject.ui.board.write

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.baek.untitledproject.R
import com.baek.untitledproject.databinding.FragmentRecruitFormSettingBinding
import com.baek.untitledproject.databinding.ItemCustomQuestionBinding
import com.baek.untitledproject.domain.data.Post
import com.baek.untitledproject.domain.utils.Result
import kotlinx.coroutines.launch

class RecruitFormSettingFragment : Fragment() {

    private var _binding: FragmentRecruitFormSettingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BoardWriteViewModel by hiltNavGraphViewModels(R.id.write_board_nav_graph)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecruitFormSettingBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCustomQuestions()
        observeEditingPost()
        setupBottomNav()
        observeSubmitState()
        setCompleteBtnEnable()
    }

    private fun observeEditingPost() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.editingPost.collect { post ->
                    render(post)
                }
            }

        }
    }

    //화면 초기화
    private fun render(post: Post) = with(binding) {
        // 체크 박스 초기화
        nameCheckBox.isChecked = post.requiresName
        genderCheckBox.isChecked = post.requiresGender
        ageCheckBox.isChecked = post.requiresAge
        majorCheckBox.isChecked = post.requiresDepartment
        studentNumberCheckBox.isChecked = post.requiresStudentId
        phoneCheckBox.isChecked = post.requiresPhone

        // 커스텀 질문 초기화
        customQuestionContainer.removeAllViews()
        post.customQuestions.forEach { q -> addCustomQuestion(q) }

        // 완료 버튼 상태 : 1개 이상 선택 시
        completeBtn.isEnabled = listOf(
            post.requiresName,
            post.requiresGender,
            post.requiresAge,
            post.requiresDepartment,
            post.requiresStudentId,
            post.requiresPhone
        ).any { it }
        if (viewModel.isLoaded) {
            completeBtn.text = "수정 저장하기"
        }
    }

    private fun setupCustomQuestions() {
        binding.addQuestionBtn.setOnClickListener {
            //TODO: 추천 질문?
            addCustomQuestion("추천 질문", true)
        }
    }

    private fun addCustomQuestion(recommendedQ: String = "", requestFocus: Boolean = false) {
        val itemBinding = ItemCustomQuestionBinding.inflate(
            layoutInflater, binding.customQuestionContainer, true
        )

        val input = itemBinding.questionInput
        input.hint = recommendedQ

        itemBinding.removeBtn.setOnClickListener {
            binding.customQuestionContainer.removeView(itemBinding.root)
        }

        if (requestFocus) {
            input.post {
                input.requestFocus()
                val imm = requireContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT)
            }
        }
    }

    //완료 버튼 활성화 여부 설정
    private fun setCompleteBtnEnable() = with(binding) {
        val boxes = listOf(
            nameCheckBox, genderCheckBox, ageCheckBox,
            majorCheckBox, studentNumberCheckBox, phoneCheckBox
        )

        fun updateEnable() {
            completeBtn.isEnabled = boxes.any { it.isChecked }
        }

        // 체크 상태 변할 때마다 enable 갱신
        boxes.forEach { cb ->
            cb.addOnCheckedStateChangedListener { _, _ -> updateEnable() }
        }

    }


    //이전/다음 버튼 이동 설정
    private fun setupBottomNav() {
        binding.prevBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.completeBtn.setOnClickListener {
            updateData()
            viewModel.completePost()
        }
    }

    //viewModel에 저장
    private fun updateData() = with(binding) {
        viewModel.updateRequirements(
            name = nameCheckBox.isChecked,
            gender = genderCheckBox.isChecked,
            age = ageCheckBox.isChecked,
            dept = majorCheckBox.isChecked,
            studentId = studentNumberCheckBox.isChecked,
            phone = phoneCheckBox.isChecked
        )
        viewModel.updateCustomQuestions(collectQuestions())
    }

    //작성한 내용이 있는 질문들만 수집
    private fun collectQuestions(): List<String> {
        val result = mutableListOf<String>()
        val container = binding.customQuestionContainer

        for (i in 0 until container.childCount) {
            val child = container.getChildAt(i)
            val input = child.findViewById<EditText>(R.id.questionInput)
            val text = input.text?.toString()?.trim().orEmpty()
            if (text.isNotBlank()) result.add(text)
        }
        return result
    }

    private fun observeSubmitState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.submitResult.collect { result ->
                    when (result) {
                        is Result.Loading -> {
                            //TODO: 로딩 UI 적용
                        }

                        is Result.Success -> {
                            if (viewModel.isLoaded) {
                                Toast.makeText(requireContext(), "공고가 수정 되었어요", Toast.LENGTH_SHORT)
                                    .show()
                                findNavController().popBackStack(
                                    R.id.write_board_nav_graph, /*inclusive=*/
                                    true
                                )
                            } else {
                                findNavController().navigate(
                                    R.id.postCompleteFragment,
                                    null,
                                    navOptions {
                                        //backstack 제거
                                        popUpTo(R.id.infoWriteFragment) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                )
                            }

                        }

                        is Result.Error -> {
                            //TODO: 에러 표시
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        //뒤로가기로 화면이 사라질때
        if (isRemoving && !requireActivity().isChangingConfigurations) {
            updateData()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}