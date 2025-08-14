package com.baek.untitledproject.ui.board.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.baek.untitledproject.databinding.FragmentConfirmDeleteBinding
import com.baek.untitledproject.ui.board.dialogs.BoardDialogKeys.KEY_CONFIRMED
import com.baek.untitledproject.ui.board.dialogs.BoardDialogKeys.REQ_CONFIRM_DELETE
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ConfirmDeleteFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentConfirmDeleteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConfirmDeleteBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cancelBtn.setOnClickListener {
            dismiss()
        }
        binding.deleteBtn.setOnClickListener {
            parentFragmentManager.setFragmentResult(
                REQ_CONFIRM_DELETE,
                bundleOf(KEY_CONFIRMED to true)
            )
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}