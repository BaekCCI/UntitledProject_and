package com.baek.untitledproject.ui.board.write

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.baek.untitledproject.R
import com.baek.untitledproject.databinding.FragmentInterviewSettingBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@AndroidEntryPoint
class InterviewSettingFragment : Fragment() {

    private var _binding: FragmentInterviewSettingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BoardWriteViewModel by hiltNavGraphViewModels(R.id.write_board_nav_graph)

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
        observeEditingPost()
        setupInterviewOptionToggle()
        setupInterviewDateSelectBtn()
        setupBottomNav()
    }

    //면접 여부 변경 관찰
    private fun observeEditingPost() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.editingPost.map { it.hasInterview }
                    .distinctUntilChanged()
                    .collect { has ->
                        has?.let { updateInterviewUI(has) }
                    }
            }
        }
    }

    //면접 여부 버튼 설정
    private fun setupInterviewOptionToggle() {
        binding.interviewYesBtn.setOnClickListener { viewModel.setHasInterview(true) }
        binding.interviewNoBtn.setOnClickListener { viewModel.setHasInterview(false) }
    }

    //면접 여부에 따른 UI 설정
    private fun updateInterviewUI(hasInterview: Boolean) {
        binding.interviewYesBtn.isChecked = hasInterview
        binding.interviewNoBtn.isChecked = !hasInterview
        binding.interviewSettingLayout.visibility = if (hasInterview) View.VISIBLE else View.GONE
        binding.nextBtn.isEnabled = true
    }


    //면접 일정 설정 화면으로 이동
    private fun setupInterviewDateSelectBtn() {
        binding.interviewDateSelectBtn.setOnClickListener {
            val action =
                InterviewSettingFragmentDirections.actionInterviewSettingFragmentToInterviewScheduleFragment()
            findNavController().navigate(action)
        }
    }

    //이전/다음 버튼 이동 설정
    private fun setupBottomNav() {
        binding.prevBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.nextBtn.setOnClickListener {
            //TODO: viewModel에 면접 장소 업데이트
            val action =
                InterviewSettingFragmentDirections.actionInterviewSettingFragmentToRecruitFormSettingFragment()
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}