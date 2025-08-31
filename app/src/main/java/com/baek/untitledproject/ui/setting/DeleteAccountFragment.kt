package com.baek.untitledproject.ui.setting

import android.os.Bundle
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
import com.baek.untitledproject.R
import com.baek.untitledproject.databinding.FragmentDeleteAccountBinding
import com.baek.untitledproject.domain.utils.Result
import com.baek.untitledproject.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DeleteAccountFragment : Fragment() {

    private var _binding: FragmentDeleteAccountBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DeleteAccountViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeleteAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.pwInput.doOnTextChanged { _, _, _, _ ->
            setConfirmBtnState()
            viewModel.onPasswordInputChanged(binding.pwInput.text.toString())
        }
        setConfirmBtn()
        setupDialog()
        observeReAuthState()
        observeDeleteState()
    }

    private fun setConfirmBtnState() {
        binding.confirmBtn.isEnabled = (binding.pwInput.text?.length ?: 0) >= 6
    }

    private fun setConfirmBtn() {
        binding.confirmBtn.setOnClickListener {
            DeleteAccountBottomSheetFragment().show(parentFragmentManager, "delete_account_dialog")

        }
    }

    private fun setupDialog() {
        parentFragmentManager.setFragmentResultListener(
            "req_delete",
            viewLifecycleOwner
        ) { _, bundle ->
            if (bundle.getBoolean("confirmed", false)) {
                //비밀 번호 검증
                viewModel.verifyPassword(binding.pwInput.text.toString())
            }
        }
    }

    private fun observeReAuthState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.reAuthState.collect { state ->

                    when (state) {
                        is Result.Success -> {
                            setPwLayout()
                            viewModel.deleteAccount(state.data)
                        }

                        is Result.Error -> {
                            Toast.makeText(
                                requireContext(),
                                "비밀번호가 올바르지 않아요. 다시 입력해주세요",
                                Toast.LENGTH_LONG
                            ).show()

                            setPwLayout(true)
                        }

                        else -> {
                            setPwLayout()
                        }
                    }

                }
            }
        }
    }

    private fun observeDeleteState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.deleteState.collect { state ->

                    when (state) {
                        is Result.Success -> {
                            findNavController().popBackStack()
                        }

                        is Result.Error -> {
                            //TODO: 탈퇴 실패 시 에러
                        }

                        else -> {
                        }
                    }

                }
            }
        }
    }

    private fun setPwLayout(isError: Boolean = false) {
        binding.pwLayout.error = when {
            isError -> "비밀번호가 올바르지 않아요"
            else -> null
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.setToolbar(detailVisible = true, title = "비밀번호 인증")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}