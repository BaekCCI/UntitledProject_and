package com.baek.untitledproject.ui.board.write

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.baek.untitledproject.R
import com.baek.untitledproject.databinding.FragmentInterviewSettingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InterviewSettingFragment : Fragment() {

    private var _binding: FragmentInterviewSettingBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInterviewSettingBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInterviewOptionToggle()
        setupInterviewDateSelectBtn()
    }

    private fun setupInterviewOptionToggle() {
        binding.interviewYesBtn.setOnClickListener {
            binding.interviewYesBtn.isChecked = true
            binding.interviewNoBtn.isChecked = false
            binding.interviewSettingLayout.visibility = View.VISIBLE
        }

        binding.interviewNoBtn.setOnClickListener {
            binding.interviewYesBtn.isChecked = false
            binding.interviewNoBtn.isChecked = true
            binding.interviewSettingLayout.visibility = View.GONE
        }
    }

    private fun setupInterviewDateSelectBtn(){
        binding.interviewDateSelectBtn.setOnClickListener {
            val action =
                InterviewSettingFragmentDirections.actionInterviewSettingFragmentToInterviewScheduleFragment()
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}