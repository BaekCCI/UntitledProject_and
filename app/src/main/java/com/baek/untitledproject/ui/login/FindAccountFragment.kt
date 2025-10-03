package com.baek.untitledproject.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.baek.untitledproject.databinding.FragmentFindAccountBinding
import com.baek.untitledproject.domain.utils.Result
import com.baek.untitledproject.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FindAccountFragment : Fragment() {

    private var _binding: FragmentFindAccountBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FindAccountViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFindAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        manageTextChanged()
        setupResetBtn()
        observeSetPasswordState()
    }

    private fun manageTextChanged() {
        binding.emailInput.doOnTextChanged { _, _, _, _ ->
            viewModel.resetSendState()
            setupResetBtnState()
            binding.emailLayout.error = null
        }
    }

    private fun setupResetBtnState() {
        binding.resetBtn.isEnabled = (binding.emailInput.text?.length ?: 0) > 0
    }

    private fun setupResetBtn() {
        binding.resetBtn.setOnClickListener {
            binding.emailInput.clearFocus()

            if (!isValidEmail()) {
                binding.emailLayout.error = "이메일 주소를 정확하게 입력해주세요"
                return@setOnClickListener
            }
            viewModel.sendEmail(binding.emailInput.text.toString())
        }
    }

    private fun observeSetPasswordState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.sendState.collect { state ->
                    when (state) {
                        is Result.Loading -> {
                            //TODO: 로딩
                        }

                        is Result.Success -> {
                            binding.emailLayout.helperText = "이메일로 새로운 비밀번호를 보내드렸어요"
                            binding.resetBtn.isEnabled = false
                        }

                        is Result.Error -> {
                            binding.emailLayout.error = state.message
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    private fun isValidEmail(): Boolean {
        val text = binding.emailInput.text?.toString().orEmpty()
        return text.endsWith(EMAIL_DOMAIN)
    }


    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.setToolbar(xToolbarVisible = true, title = "")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}