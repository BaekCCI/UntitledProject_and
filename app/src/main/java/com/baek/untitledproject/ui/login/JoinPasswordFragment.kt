package com.baek.untitledproject.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.baek.untitledproject.R
import com.baek.untitledproject.databinding.FragmentJoinPasswordBinding
import com.baek.untitledproject.domain.utils.Result
import com.baek.untitledproject.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class JoinPasswordFragment : Fragment() {

    private var _binding: FragmentJoinPasswordBinding? = null
    private val binding get() = _binding!!

    private val joinViewModel: JoinViewModel by hiltNavGraphViewModels(R.id.login_nav_graph)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJoinPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setEmailField()
        manageTextChanged()
        setupNextBtn()
        observeSetPasswordState()
    }

    private fun setEmailField() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                joinViewModel.signInState.collect { state ->
                    when (state) {
                        is Result.Success -> {
                            binding.emailInput.setText(state.data.email)
                        }

                        else -> {
                        }
                    }
                }
            }
        }
    }

    //텍스트 변경 감지
    private fun manageTextChanged() {
        binding.pwInput.doOnTextChanged { _, _, _, _ ->
            setupNextBtnState()
            validatePassword()
        }
        binding.checkPwInput.doOnTextChanged { _, _, _, _ ->
            setupNextBtnState()
            validateCheckPassword()
        }
    }

    //비밀번호 6자리 이상 입력 검증 -> error 설정
    private fun validatePassword() {
        val text = binding.pwInput.text?.toString().orEmpty()
        binding.pwLayout.error = when {
            text.isEmpty() -> null
            text.length < 6 -> "6자리 이상 입력해주세요"
            else -> null
        }
    }

    //비밀번호 확인 입력 일치 검증 -> error 설정
    private fun validateCheckPassword() {
        val pw = binding.pwInput.text?.toString().orEmpty()
        val text = binding.checkPwInput.text?.toString().orEmpty()
        binding.checkPwLayout.error = when {
            text.isEmpty() -> null
            pw != text -> "비밀번호가 일치하지 않습니다"
            else -> null
        }
    }


    //입력값 검증
    private fun isValidPassword(): Boolean {
        return (binding.pwInput.text?.length ?: 0) >= 6
    }

    private fun isValidCheckPassword(): Boolean {
        val pw = binding.pwInput.text?.toString().orEmpty()
        val check = binding.checkPwInput.text?.toString().orEmpty()
        return check.isNotEmpty() && pw == check
    }

    //다음 버튼 활성화 설정
    private fun setupNextBtnState() {
        val enabled = isValidPassword() && isValidCheckPassword()
        binding.nextBtn.isEnabled = enabled
    }

    private fun setupNextBtn() {
        binding.nextBtn.setOnClickListener {
            joinViewModel.setPassword(binding.pwInput.text.toString())
        }
    }

    private fun observeSetPasswordState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                joinViewModel.setPwState.collect { state ->
                    when (state) {
                        is Result.Loading -> {
                            //TODO: 로딩
                        }

                        is Result.Success -> {
                            val action =
                                JoinPasswordFragmentDirections.actionJoinPasswordFragmentToJoinInfoFragment()
                            findNavController().navigate(action)
                        }

                        is Result.Error -> {

                        }

                        else -> {}
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