package com.baek.untitledproject.ui.board.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.baek.untitledproject.databinding.FragmentMoreActionBottomSheetBinding
import com.baek.untitledproject.ui.board.dialogs.BoardDialogKeys.ACTION_DELETE
import com.baek.untitledproject.ui.board.dialogs.BoardDialogKeys.ACTION_EDIT
import com.baek.untitledproject.ui.board.dialogs.BoardDialogKeys.KEY_ACTION
import com.baek.untitledproject.ui.board.dialogs.BoardDialogKeys.REQ_MORE_ACTION
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MoreActionBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentMoreActionBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoreActionBottomSheetBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.editBtn.setOnClickListener {
            setFragmentResult(REQ_MORE_ACTION, bundleOf(KEY_ACTION  to ACTION_EDIT))
            dismiss()
        }
        binding.deleteBtn.setOnClickListener {
            setFragmentResult(REQ_MORE_ACTION , bundleOf(KEY_ACTION to ACTION_DELETE))
            dismiss()
        }
        binding.closeBtn.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}