package com.baek.untitledproject.ui.board.write

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
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
        setupBackHandler()
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
        if (viewModel.submitResult.value is Result.Loading) return
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

        if (viewModel.isApplicantExist) {
            addQuestionBtn.visibility = View.GONE
        }
        if (viewModel.isLoaded) {
            completeBtn.text = "수정 저장하기"

        }
    }

    private fun setupCustomQuestions() {
        binding.addQuestionBtn.setOnClickListener {
            //TODO: 추천 질문?
            addCustomQuestion(requestFocus = true)
        }
    }

    private fun addCustomQuestion(question: String = "", requestFocus: Boolean = false) {
        val itemBinding = ItemCustomQuestionBinding.inflate(
            layoutInflater, binding.customQuestionContainer, true
        )

        val input = itemBinding.questionInput
        input.setText(question)

        if (viewModel.isApplicantExist) {
            lockQuestionRow(itemBinding)
        }
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
    @SuppressLint("ClickableViewAccessibility")
    private fun setCompleteBtnEnable() = with(binding) {
        val boxes = listOf(
            nameCheckBox, genderCheckBox, ageCheckBox,
            majorCheckBox, studentNumberCheckBox, phoneCheckBox
        )

        fun updateEnable() {
            completeBtn.isEnabled = boxes.any { it.isChecked }
        }
        if (viewModel.isApplicantExist) {
            boxes.forEach { cb ->
                cb.setOnTouchListener { _, event ->
                    if (event.action == MotionEvent.ACTION_UP) {
                        showApplicantExistAlert()
                    }
                    true // ← 이벤트 소비: 체크 토글 자체가 일어나지 않음
                }
            }
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
            binding.root.clearFocus()
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
            phone = phoneCheckBox.isChecked,
            collectQuestions()
        )
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

    private fun lockQuestionRow(item: ItemCustomQuestionBinding) {
        val input = item.questionInput

        // 수정 불가 설정 (키보드/커서/포커스 차단)
        input.keyListener = null            // 입력 자체 비활성
        input.isFocusable = false
        input.isFocusableInTouchMode = false
        input.isCursorVisible = false
        input.isLongClickable = false

        // 클릭하면 알림만
        input.setOnClickListener { showApplicantExistAlert() }
        input.setOnLongClickListener {
            showApplicantExistAlert()
            true
        }

        // 삭제 버튼도 막기
        item.removeBtn.visibility = View.GONE
    }

    private fun showApplicantExistAlert() {
        Toast.makeText(requireContext(), "지원자가 존재하여 수정이 불가능 합니다.", Toast.LENGTH_SHORT).show()
    }

    private fun setupBackHandler() {
        // 시스템 뒤로가기(제스처 포함) 가로채기
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    updateData()
                    findNavController().popBackStack()

                }
            }
        )

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}