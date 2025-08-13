package com.baek.untitledproject.ui.board.apply

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.baek.untitledproject.R
import com.baek.untitledproject.databinding.FragmentApplicationFormBinding
import com.baek.untitledproject.ui.MainActivity

class ApplicationFormFragment : Fragment() {

    private var _binding: FragmentApplicationFormBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentApplicationFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.setToolbar(detailVisible = true, title = "신청서 작성")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}