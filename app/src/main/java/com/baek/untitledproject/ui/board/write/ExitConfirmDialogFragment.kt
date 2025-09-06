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
}