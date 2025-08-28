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
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navOptions
import com.baek.untitledproject.R
import com.baek.untitledproject.data.local.model.AuthCache
import com.baek.untitledproject.data.local.model.EmailLinkResult
import com.baek.untitledproject.databinding.FragmentEmailVerifyBinding
import com.baek.untitledproject.domain.utils.Result
import com.baek.untitledproject.ui.login.AuthEntry.Companion.toEntry
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EmailVerifyFragment : Fragment() {

    private var _binding: FragmentEmailVerifyBinding? = null
    private val binding get() = _binding!!

    private val emailVerifyViewModel: EmailVerifyViewModel by viewModels()
    private val loginViewModel: LoginViewModel by hiltNavGraphViewModels(R.id.login_nav_graph)
    private val args: EmailVerifyFragmentArgs by navArgs()

    private var entry: AuthEntry? = null
    private var consumedDeepLink = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEmailVerifyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        entry = args.entry.toEntry()

        setEmailInputFormat()
        setEmailVerifyBtn()
        setSendEmailBtn()
        observeSignInState()
        observeSendState()

        observeLoginState()
        observeCanLogin()
    }

    //인증 메일 요청 버튼
    private fun setSendEmailBtn() {

        binding.sendEmailBtn.setOnClickListener {
            val email = binding.emailInput.text.toString().trim()
            Log.d("EmailVerifyFragment", "email $email")
            emailVerifyViewModel.requestEmailLink(email, entry ?: AuthEntry.JOIN)
        }
    }

    //인증 확인 버튼
    private fun setEmailVerifyBtn() {
        binding.EmailVerifyBtn.setOnClickListener {
            handleSignInState(emailVerifyViewModel.signInState.value)
        }
    }

    private fun setEmailInputFormat() {
        val edit = binding.emailInput
        var isUpdating = false

        edit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s == null) {
                    binding.sendEmailBtn.isEnabled = false
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

    //인증 요청 상태
    private fun observeSendState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                emailVerifyViewModel.sendState.collect { state ->
                    when (state) {
                        is Result.Success -> {
                            binding.sendEmailBtn.visibility = View.GONE
                            binding.EmailVerifyBtn.visibility = View.VISIBLE
                            Toast.makeText(
                                requireContext(),
                                "인증 요청 이메일을 보냈어요!\n메일함을 확인한 뒤, 인증 확인 버튼을 눌러주세요",
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }

                        else -> {
                            binding.sendEmailBtn.visibility = View.VISIBLE
                            binding.EmailVerifyBtn.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    //-----------------
    //이메일 인증 상태
    private fun observeSignInState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                emailVerifyViewModel.signInState.collect { state ->
                    handleSignInState(state)
                }
            }
        }
    }

    private fun handleSignInState(state: Result<EmailLinkResult>) {
        when (state) {
            is Result.Loading -> {
                // 필요 시 로딩 UI
            }

            is Result.Success -> {
                proceedAfterSignIn(state.data)
            }

            is Result.Error -> {
                showSignInError()
            }

            else -> Unit
        }
    }

    //isNewUser == false => userExists? : false -> 회원가입, true -> 로그인
    //isNewUser == true => 회원가입
    //인증 성공 후 신규/기존 분기
    private fun proceedAfterSignIn(result: EmailLinkResult) {
        loginViewModel.acceptEmailResult(result)
        if (result.isNewUser) {
            if (entry == AuthEntry.JOIN) {
                Toast.makeText(requireContext(), "인증을 성공하였습니다.", Toast.LENGTH_LONG).show()
            } else if (entry == AuthEntry.LOGIN) {
                Toast.makeText(requireContext(), "가입이력이 확인되지 않아요!\n회원가입을 진행해주세요", Toast.LENGTH_LONG)
                    .show()
            }

            clearState()
            val action = EmailVerifyFragmentDirections
                .actionEmailVerifyFragmentToJoinInfoFragment()
            findNavController().navigate(action)
        } else {
            loginViewModel.existsUser()
        }
    }

    private fun observeCanLogin() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                loginViewModel.canLogin.collect { state ->
                    when (state) {
                        is Result.Success -> {
                            if (state.data) { //가입 정보가 존재하면
                                loginViewModel.login()
                            } else {
                                if (entry == AuthEntry.JOIN) {
                                    Toast.makeText(
                                        requireContext(),
                                        "인증을 성공하였습니다.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else if (entry == AuthEntry.LOGIN) {
                                    Toast.makeText(
                                        requireContext(),
                                        "가입이력이 확인되지 않아요!\n회원가입을 진행해주세요",
                                        Toast.LENGTH_LONG
                                    )
                                        .show()
                                }
                                clearState()

                                val action = EmailVerifyFragmentDirections
                                    .actionEmailVerifyFragmentToJoinInfoFragment()
                                findNavController().navigate(action)

                            }
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    private fun observeLoginState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                loginViewModel.loginState.collect { state ->
                    when (state) {
                        is Result.Loading -> {

                        }

                        is Result.Success -> {
                            Toast.makeText(requireContext(), "성공적으로 로그인했어요!", Toast.LENGTH_LONG)
                                .show()
                            emailVerifyViewModel.clearAuthCache()
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

                        }

                        else -> {}
                    }
                }
            }
        }
    }

    //인증 실패 시
    private fun showSignInError() {
        Toast.makeText(
            requireContext(),
            "인증을 실패하였습니다. 이메일을 확인해주세요",
            Toast.LENGTH_LONG
        ).show()
    }

    //인증 완료 후 돌아왔을 때 기존에 입력했던 이메일 값
    private fun emailFieldFromCacheIfDeepLinked() {
        if (!consumedDeepLink) return
        viewLifecycleOwner.lifecycleScope.launch {
            // authCache가 Success로 emit될 때까지 '한 번만' 기다린 뒤 채우기
            val email = emailVerifyViewModel.authCache
                .filterIsInstance<Result.Success<AuthCache>>()
                .map { it.data.email.orEmpty() }
                .firstOrNull()

            if (binding.emailInput.text.isNullOrBlank() && !email.isNullOrBlank()) {
                binding.emailInput.setText(email)
            }
        }
    }

    private fun clearState() {
        emailVerifyViewModel.clearAuthCache()
        loginViewModel.clearCanLogin()
    }

    override fun onStart() {
        super.onStart()
        // 화면이 보일 때마다 확인하되, 한 번만 처리
        if (!consumedDeepLink) {
            requireActivity().intent?.data?.let { uri ->
                emailVerifyViewModel.handleDeepLink(uri)
                // 중복 호출 방지
                consumedDeepLink = true
                emailFieldFromCacheIfDeepLinked()
                // 다음 진입에서 다시 처리되지 않게 인텐트 정리(선택)
                requireActivity().intent?.data = null
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}