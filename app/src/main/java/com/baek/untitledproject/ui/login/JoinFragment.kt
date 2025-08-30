package com.baek.untitledproject.ui.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.baek.untitledproject.R
import com.baek.untitledproject.databinding.FragmentJoinBinding
import com.baek.untitledproject.domain.utils.Result
import com.baek.untitledproject.ui.MainActivity
import kotlinx.coroutines.launch

class JoinFragment : Fragment() {

    private var _binding: FragmentJoinBinding? = null
    private val binding get() = _binding!!

    private val joinViewModel: JoinViewModel by hiltNavGraphViewModels(R.id.login_nav_graph)

    private var consumedDeepLink = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJoinBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        joinViewModel.lastRequestedEmail?.let { binding.emailInput.setText(it) }

        setEmailInputFormat()
        setSendEmailBtn()
        observeSendState()
        observeCache()
        setEmailVerifyBtn()
    }

    //이메일 입력필드 설정
    private fun setEmailInputFormat() {
        val edit = binding.emailInput
        var isUpdating = false

        edit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s == null) {
                    binding.sendEmailBtn.isEnabled = false
                    joinViewModel.onEmailInputChanged("")
                    return
                }
                if (isUpdating) return

                // IME 조합(한글) 중이면 건너뛰기
                val composing =
                    s.getSpans(0, s.length, android.text.style.SuggestionSpan::class.java)
                if (composing.isNotEmpty()) return

                val raw = s.toString()

                // 1) 로컬/도메인 분리
                val local = raw.substringBefore("@").replace("\\s".toRegex(), "")
                // 2) 최종 문자열: local + @domain
                val normalized = if (local.isEmpty()) "" else local + EMAIL_DOMAIN

                // 3) 이미 정상 형태면 패스
                if (normalized == raw) {
                    updateSendButtonEnabled(normalized)
                    return
                }
                joinViewModel.onEmailInputChanged(normalized)

                isUpdating = true
                edit.setText(normalized)
                // 커서를 항상 @ 앞에
                edit.setSelection(local.length.coerceAtLeast(0))
                isUpdating = false
                updateSendButtonEnabled(normalized)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun updateSendButtonEnabled(email: String) {
        val isValid = email.endsWith(EMAIL_DOMAIN)
        binding.sendEmailBtn.isEnabled = isValid
    }

    // 인증 요청 --------------

    //인증 메일 요청 버튼
    private fun setSendEmailBtn() {

        binding.sendEmailBtn.setOnClickListener {
            val email = binding.emailInput.text.toString().trim()
            joinViewModel.requestEmailLink(email)
        }
    }

    //인증 요청 상태
    private fun observeSendState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                joinViewModel.sendState.collect { state ->
                    when (state) {
                        is Result.Success -> {
                            setUiRequested(binding.emailInput.text.toString())
                            Toast.makeText(
                                requireContext(),
                                "인증 요청 이메일을 보냈어요!\n메일함을 확인한 뒤, 인증 확인 버튼을 눌러주세요",
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }
                        is Result.Error ->{

                        }

                        else -> {
                            setUiDefault()
                        }
                    }
                }
            }
        }
    }

    private fun observeCache() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                joinViewModel.emailCacheFlow.collect { cachedEmail ->
                    if (consumedDeepLink) {
                        val email = cachedEmail.orEmpty()
                        if (email.isNotEmpty()) {
                            setUiRequested(email)
                        }
                    }

                }
            }
        }
    }

    private fun setUiDefault() {
        binding.sendEmailBtn.visibility = View.VISIBLE
        binding.EmailVerifyBtn.visibility = View.GONE
        binding.emailInputLauout.helperText = " "
        binding.emailInputLauout.error = null
    }

    private fun setUiRequested(email: String) {
        binding.emailInput.setText(email)
        binding.sendEmailBtn.visibility = View.GONE
        binding.EmailVerifyBtn.visibility = View.VISIBLE
        binding.emailInputLauout.helperText = " "
        binding.emailInputLauout.error = null
    }

    //인증 확인 버튼
    private fun setEmailVerifyBtn() {
        binding.EmailVerifyBtn.setOnClickListener {
            checkSignInState()
        }
    }

    //인증 상태 확인
    private fun checkSignInState() {
        when (val state = joinViewModel.signInState.value) {
            is Result.Success -> {
                val emailResult = state.data

                if (emailResult.isNewUser) {
                    Toast.makeText(
                        requireContext(),
                        "인증을 성공하였습니다.",
                        Toast.LENGTH_LONG
                    )
                        .show()
                    val action = JoinFragmentDirections.actionJoinFragmentToJoinPasswordFragment()
                    findNavController().navigate(action)
                } else {
                    //TODO: 이미 존재하는 계정?
                }
                binding.emailInputLauout.error = null
            }

            is Result.Error -> {
                Toast.makeText(
                    requireContext(),
                    "인증을 실패하였습니다. 이메일을 확인해주세요",
                    Toast.LENGTH_LONG
                ).show()
                binding.emailInputLauout.error = "이메일을 확인해주세요"

            }

            else -> {
                binding.emailInputLauout.error = null
            }
        }
    }


    override fun onStart() {
        super.onStart()
        // 화면이 보일 때마다 확인하되, 한 번만 처리
        if (!consumedDeepLink) {
            requireActivity().intent?.data?.let { uri ->
                joinViewModel.handleDeepLink(uri)
                // 중복 호출 방지
                consumedDeepLink = true

                // 다음 진입에서 다시 처리되지 않게 인텐트 정리(선택)
                requireActivity().intent?.data = null
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