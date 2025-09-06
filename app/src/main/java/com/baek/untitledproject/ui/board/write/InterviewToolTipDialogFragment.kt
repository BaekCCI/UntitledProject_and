package com.baek.untitledproject.ui.board.write

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.baek.untitledproject.R
import com.baek.untitledproject.databinding.FragmentInterviewSettingBinding
import com.baek.untitledproject.databinding.FragmentInterviewToolTipDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class InterviewToolTipDialogFragment : BottomSheetDialogFragment() {

    private var _binding : FragmentInterviewToolTipDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInterviewToolTipDialogBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.completeBtn.setOnClickListener { dismiss() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}