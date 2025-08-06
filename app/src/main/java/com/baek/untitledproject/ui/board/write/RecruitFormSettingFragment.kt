package com.baek.untitledproject.ui.board.write

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.baek.untitledproject.R
import com.baek.untitledproject.databinding.FragmentRecruitDateSelectDialogBinding
import com.baek.untitledproject.databinding.FragmentRecruitFormSettingBinding

class RecruitFormSettingFragment : Fragment() {

    private var _binding: FragmentRecruitFormSettingBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecruitFormSettingBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}