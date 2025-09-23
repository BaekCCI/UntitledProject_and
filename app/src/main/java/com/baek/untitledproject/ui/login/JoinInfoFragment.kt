package com.baek.untitledproject.ui.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.baek.untitledproject.R
import com.baek.untitledproject.databinding.FragmentJoinInfoBinding
import com.baek.untitledproject.domain.utils.Result
import com.baek.untitledproject.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class JoinInfoFragment : Fragment() {

    private var _binding: FragmentJoinInfoBinding? = null
    private val binding get() = _binding!!

    private val joinViewModel: JoinViewModel by hiltNavGraphViewModels(R.id.login_nav_graph)


    private val editTexts by lazy {
        listOf(
            binding.nameInput,
            binding.birthInput,
            binding.departmentInput,
            binding.studentIdInput
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJoinInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTextWatchers()
        setupGenderButton()
        setupAgreementCheckBox()
        setupTermSheet()
        setupCompleteBtn()

        observeJoinState()
    }

    //텍스트 변경 감지
    private fun setupTextWatchers() {
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                setupCompleteBtnState()
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        }
        editTexts.forEach { it.addTextChangedListener(watcher) }
    }

    //성별 버튼 클릭
    private fun setupGenderButton() {
        binding.maleBtn.setOnClickListener { toggleGenderBtn(true) }
        binding.femaleBtn.setOnClickListener { toggleGenderBtn(false) }
    }

    //성별 버튼 상태 토글
    private fun toggleGenderBtn(isMale: Boolean) = with(binding) {
        maleBtn.isChecked = isMale
        femaleBtn.isChecked = !isMale
        setupCompleteBtnState()
    }

    //동의 버튼
    private fun setupAgreementCheckBox() {
        binding.agreeTerms.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.termsTxt.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.point_skyblue
                    )
                )
                binding.termsIcon.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.point_skyblue
                    )
                )
            } else {
                binding.termsTxt.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.gray_500
                    )
                )
                binding.termsIcon.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.gray_300
                    )
                )
            }
            setupCompleteBtnState()
        }
        binding.agreePrivacyBtn.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.privacyTxt.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.point_skyblue
                    )
                )
                binding.privacyIcon.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.point_skyblue
                    )
                )
            } else {
                binding.privacyTxt.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.gray_500
                    )
                )
                binding.privacyIcon.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.gray_300
                    )
                )
            }
            setupCompleteBtnState()
        }
    }

    //완료 버튼 enable 설정
    private fun setupCompleteBtnState() {
        val allFilled = editTexts.all { it.text?.isNotBlank() == true }
        val genderSelected = binding.maleBtn.isChecked || binding.femaleBtn.isChecked
        val allAgreed = binding.agreeTerms.isChecked && binding.agreePrivacyBtn.isChecked
        binding.completeBtn.isEnabled = allFilled && genderSelected && allAgreed
    }

    private fun setupTermSheet() {
        binding.termsDetailsBtn.setOnClickListener {
            TermSheet.newInstance(
                title = "서비스 이용약관",
                content = "이용약관 내용"//getString("이용약관 내용")
            ).apply {
                onConfirm = {
                    binding.agreeTerms.isChecked = true
                    setupCompleteBtnState()
                }
            }.show(parentFragmentManager, "terms_sheet")
        }
        binding.privacyDetailsBtn.setOnClickListener {
            TermSheet.newInstance(
                title = "개인정보 처리방침",
                content = "개인정보 처리방침 내용"//getString("이용약관 내용")
            ).apply {
                onConfirm = {
                    binding.agreePrivacyBtn.isChecked = true
                    setupCompleteBtnState()
                }
            }.show(parentFragmentManager, "terms_sheet")
        }
    }

    private fun setupCompleteBtn() = with(binding) {

        completeBtn.setOnClickListener {
            joinViewModel.completeJoin(
                name = nameInput.text.toString(),
                birth = birthInput.text.toString(),
                department = departmentInput.text.toString(),
                studentId = studentIdInput.text.toString(),
                gender = if (maleBtn.isChecked) "M" else "F"
            )
        }
    }

    private fun observeJoinState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                joinViewModel.joinState.collect { state ->
                    when (state) {
                        is Result.Loading -> {
                            //TODO: 로딩
                        }

                        is Result.Success -> {
                            val action = JoinInfoFragmentDirections
                                .actionJoinInfoFragmentToCompleteJoinFragment(state.data.name)
                            findNavController().navigate(
                                action,
                                navOptions {
                                    popUpTo(R.id.login_nav_graph) { inclusive = true }
                                }
                            )
                        }

                        else -> {
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.setToolbar(detailVisible = true, title = "회원가입")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}