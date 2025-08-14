package com.baek.untitledproject.ui.board.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.baek.untitledproject.databinding.FragmentReportBottomSheetBinding
import com.baek.untitledproject.ui.board.dialogs.BoardDialogKeys.KEY_REPORTED
import com.baek.untitledproject.ui.board.dialogs.BoardDialogKeys.REQ_REPORT
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ReportBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentReportBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportBottomSheetBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.closeBtn.setOnClickListener {
            dismiss()
        }

        binding.reportBtn.setOnClickListener {
            setFragmentResult(REQ_REPORT, bundleOf(KEY_REPORTED to true))
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}