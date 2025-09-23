package com.baek.untitledproject.ui.board.write.Edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.baek.untitledproject.databinding.FragmentConfirmEditDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ConfirmEditDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentConfirmEditDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConfirmEditDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val content = requireArguments().getString(ARG_CONTENT)
            ?: "기존 시간에 면접을 예약한 사람이 있어요.\n시간이 변경되면 기존 예약은 취소돼요."
        val continueTxt = requireArguments().getString(ARG_CONFIRM) ?: "시간 변경"
        binding.content.text = content
        binding.confirmBtn.text = continueTxt

        binding.confirmBtn.setOnClickListener {
            parentFragmentManager.setFragmentResult(
                "req_confirm",
                bundleOf("confirmed" to true)
            )
            dismiss()
        }

        binding.cancelBtn.setOnClickListener { dismiss() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_CONTENT = "arg_content"
        private const val ARG_CONFIRM = "arg_confirm"

        fun newInstance(
            content: String? = "기존 시간에 면접을 예약한 사람이 있어요.\n시간이 변경되면 기존 예약은 취소돼요.",
            confirmText: String? = "시간 변경"
        ): ConfirmEditDialogFragment {
            return ConfirmEditDialogFragment().apply {
                arguments = bundleOf(
                    ARG_CONTENT to content,
                    ARG_CONFIRM to confirmText
                )
            }
        }
    }
}