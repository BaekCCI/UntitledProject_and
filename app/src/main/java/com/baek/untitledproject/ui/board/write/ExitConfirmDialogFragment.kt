package com.baek.untitledproject.ui.board.write

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.baek.untitledproject.R
import com.baek.untitledproject.databinding.FragmentExitConfirmDialogBinding
import com.baek.untitledproject.ui.board.dialogs.BoardDialogKeys.KEY_CONFIRMED
import com.baek.untitledproject.ui.board.dialogs.BoardDialogKeys.REQ_APPLICATION_EXIT
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ExitConfirmDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentExitConfirmDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExitConfirmDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val content = requireArguments().getString(ARG_CONTENT) ?: "지금까지 작성한 내용이 저장되지 않습니다\n나가시겠어요?"
        val continueTxt = requireArguments().getString(ARG_CONTINUE) ?: "계속 작성하기"
        val exitTxt = requireArguments().getString(ARG_EXIT) ?: "중단하고 나가기"


        binding.content.text = content
        binding.continueBtn.text = continueTxt
        binding.exitBtn.text = exitTxt

        binding.continueBtn.setOnClickListener { dismiss() }

        binding.exitBtn.setOnClickListener {
            parentFragmentManager.setFragmentResult(
                "req_exit",
                bundleOf("confirmed" to true)
            )
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_CONTENT = "arg_content"
        private const val ARG_CONTINUE = "arg_continue"
        private const val ARG_EXIT = "arg_exit"

        fun newInstance(
            content: String = "지금까지 작성한 내용이 저장되지 않습니다\n나가시겠어요?",
            continueText: String = "계속 작성하기",
            exitText: String = "중단하고 나가기"
        ): ExitConfirmDialogFragment {
            return ExitConfirmDialogFragment().apply {
                arguments = bundleOf(
                    ARG_CONTENT to content,
                    ARG_CONTINUE to continueText,
                    ARG_EXIT to exitText
                )
            }
        }
    }
}