package com.baek.untitledproject.ui.board.report

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.baek.untitledproject.R
import com.baek.untitledproject.databinding.FragmentReportCompleteBinding
import com.baek.untitledproject.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReportCompleteFragment : Fragment() {

    private var _binding: FragmentReportCompleteBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportCompleteBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.setToolbar()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}