package com.baek.untitledproject.ui.board.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.baek.untitledproject.R
import com.baek.untitledproject.databinding.FragmentApplicationExitDialogBinding
import com.baek.untitledproject.ui.board.dialogs.BoardDialogKeys.KEY_CONFIRMED
import com.baek.untitledproject.ui.board.dialogs.BoardDialogKeys.REQ_APPLICATION_EXIT
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ApplicationExitDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentApplicationExitDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentApplicationExitDialogBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.cancelBtn.setOnClickListener {
            dismiss()
        }
        binding.exitBtn.setOnClickListener {
            //Fragment에 삭제한다고 알림
            parentFragmentManager.setFragmentResult(
                REQ_APPLICATION_EXIT,
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