package com.baek.untitledproject.ui.board.apply

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.widget.doAfterTextChanged
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.baek.untitledproject.R
import com.baek.untitledproject.databinding.FragmentApplicationFormBinding
import com.baek.untitledproject.databinding.ItemApplicationFormBinding
import com.baek.untitledproject.domain.data.ApplicationRequirements
import com.baek.untitledproject.domain.data.CustomQuestion
import com.baek.untitledproject.domain.utils.Result
import com.baek.untitledproject.ui.MainActivity
import com.baek.untitledproject.ui.board.dialogs.BoardDialogKeys.KEY_CONFIRMED
import com.baek.untitledproject.ui.board.dialogs.BoardDialogKeys.REQ_APPLICATION_EXIT
import com.baek.untitledproject.ui.board.dialogs.BoardDialogKeys.REQ_CONFIRM_DELETE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ApplicationFormFragment : Fragment() {

    private var _binding: FragmentApplicationFormBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ApplicationViewModel by hiltNavGraphViewModels(R.id.submit_application_nav)

    private val postId: String by lazy {
        requireNotNull(requireArguments().getString("postId"))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentApplicationFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeState()
        load()
        setupSubmitBtn()
        setupBackPressListener()
        setupBackPressHandler()
    }

    private fun load() {
        viewModel.load(postId)
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.applicationRequirement.collect { state ->
                    when (state) {
                        is Result.Success -> {
                            bindData(state.data)
                        }

                        is Result.Loading -> {
                            //TODO: loading ui 적용
                        }

                        is Result.Error -> {
                            //TODO: Error 처리
                        }

                        else -> {} //None일 때는 아무 처리도 하지 않음
                    }
                }
            }
        }

    }

    private fun bindData(applicationRequirement: ApplicationRequirements) = with(binding) {

        chipGroupView.nameChip.isChecked = applicationRequirement.requiresName
        chipGroupView.departmentChip.isChecked = applicationRequirement.requiresDepartment
        chipGroupView.ageChip.isChecked = applicationRequirement.requiresAge
        //chipGroupView.phoneChip.isChecked = applicationRequirement.requiresPhone
        chipGroupView.studentIdChip.isChecked = applicationRequirement.requiresStudentId
        chipGroupView.genderChip.isChecked = applicationRequirement.requiresGender

        renderQuestions(applicationRequirement.customQuestions)
    }

    //완료 버튼 검증용
    private val questionItems =
        mutableListOf<Pair<String, ItemApplicationFormBinding>>() // (questionId, binding)


    private fun renderQuestions(questions: List<CustomQuestion>) {

        val parent = binding.questionLayout
        parent.removeAllViews()
        questionItems.clear()

        val inflater = LayoutInflater.from(requireContext())

        val saved = viewModel.answers.value
        questions.forEach { q ->
            val itemBinding = ItemApplicationFormBinding.inflate(inflater, parent, false)

            itemBinding.question.text = q.questionText
            val prev = saved[q.questionId].orEmpty()

            if (prev.isNotEmpty()) {
                itemBinding.input.setText(prev)
                itemBinding.input.setSelection(prev.length)
            }
            itemBinding.input.doAfterTextChanged { validateInputs() }

            parent.addView(itemBinding.root)
            questionItems += q.questionId to itemBinding
        }
        validateInputs()
    }

    private fun validateInputs() {
        val allFilled = questionItems
            .map { it.second.input }
            .all { !it.text.isNullOrBlank() }

        binding.submitBtn.isEnabled = allFilled
    }

    private fun setupSubmitBtn() {
        binding.submitBtn.setOnClickListener {
            val answers = questionItems.associate { (id, binding) ->
                id to binding.input.text.toString()
            }
            viewModel.saveAnswers(answers)
            //TODO: 미리보기 화면으로 이동
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.setToolbar(detailVisible = true, title = "신청서 작성")
    }

    private fun setupBackPressListener(){
        parentFragmentManager.setFragmentResultListener(
            REQ_APPLICATION_EXIT,
            viewLifecycleOwner
        ) { _, bundle ->
            if (bundle.getBoolean(KEY_CONFIRMED, false)) {
                findNavController().popBackStack()
            }
        }
    }
    private fun setupBackPressHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            val tag = "application_exit_dialog"
            if (parentFragmentManager.findFragmentByTag(tag) == null) {
                com.baek.untitledproject.ui.board.dialogs.ApplicationExitDialogFragment()
                    .show(parentFragmentManager, tag)
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        questionItems.clear()
        _binding = null
    }
}