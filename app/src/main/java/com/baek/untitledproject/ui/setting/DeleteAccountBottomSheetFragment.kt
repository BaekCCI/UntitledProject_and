package com.baek.untitledproject.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.baek.untitledproject.databinding.FragmentDeleteAccountBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DeleteAccountBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentDeleteAccountBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeleteAccountBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cancelBtn.setOnClickListener { dismiss() }

        binding.deleteAccountBtn.setOnClickListener {
            parentFragmentManager.setFragmentResult(
                "req_delete",
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