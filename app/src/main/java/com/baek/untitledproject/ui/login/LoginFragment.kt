package com.baek.untitledproject.ui.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.baek.untitledproject.R
import com.baek.untitledproject.databinding.FragmentLoginBinding
import com.baek.untitledproject.domain.utils.Result
import com.baek.untitledproject.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setEmailInputFormat()
        watchPasswordTextChanged()
        setLoginBtn()
        observeLoginState()

        binding.findAccountBtn.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToFindAccountFragment()
            findNavController().navigate(action)
        }
    }

    private fun setEmailInputFormat() {
        val edit = binding.emailInput
        var isUpdating = false

        edit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s == null) return
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
                    updateSendButtonEnabled()
                    return
                }

                isUpdating = true
                edit.setText(normalized)
                // 커서를 항상 @ 앞에
                edit.setSelection(local.length.coerceAtLeast(0))
                isUpdating = false
                updateSendButtonEnabled()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun watchPasswordTextChanged() {
        binding.pwInput.doOnTextChanged { _, _, _, _ ->
            updateSendButtonEnabled()
        }
    }

    private fun updateSendButtonEnabled() = with(binding) {
        val isValid = emailInput.text.toString().endsWith(EMAIL_DOMAIN) && pwInput.text.toString()
            .isNotEmpty()
        binding.loginBtn.isEnabled = isValid
    }

    private fun setLoginBtn() {
        binding.loginBtn.setOnClickListener {
            loginViewModel.login(
                binding.emailInput.text.toString(),
                binding.pwInput.text.toString()
            )
        }
    }

    private fun observeLoginState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                loginViewModel.loginState.collect { state ->
                    when (state) {
                        is Result.Success -> {
                            Toast.makeText(
                                requireContext(),
                                "성공적으로 로그인했어요!",
                                Toast.LENGTH_LONG
                            )
                                .show()
                            findNavController().navigate(
                                R.id.settingFragment,
                                null,
                                navOptions {
                                    popUpTo(R.id.login_nav_graph) { inclusive = true }
                                    launchSingleTop = true
                                }
                            )

                        }

                        is Result.Error -> {
                            Toast.makeText(
                                requireContext(),
                                "인증을 실패하였습니다.\n이메일과 비밀번호를 확인해주세요",
                                Toast.LENGTH_LONG
                            )
                                .show()
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
        (activity as? MainActivity)?.setToolbar(detailVisible = true, title = "로그인")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}