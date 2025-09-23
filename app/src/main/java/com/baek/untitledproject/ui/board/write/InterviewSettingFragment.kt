package com.baek.untitledproject.ui.board.write

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.baek.untitledproject.R
import com.baek.untitledproject.databinding.FragmentInterviewSettingBinding
import com.baek.untitledproject.domain.utils.DateUiStyle
import com.baek.untitledproject.domain.utils.toDateRange
import com.baek.untitledproject.ui.MainActivity
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
        setupBackHandler()
    }

    //면접 여부 변경 관찰
    private fun observeEditingPost() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.post.collect { post ->
                    val hasInterview = post.hasInterview
                    if (hasInterview != null) {
                        updateInterviewUI(hasInterview)
                        post.interviewLocation?.let { binding.interviewLocInput.setText(it) }
                    }
                    val slots = post.interviewSlot
                    if (slots.isNotEmpty()) {
                        val start = slots.keys.min()
                        val end = slots.keys.max()

                        binding.interviewDateSelectBtn.text = toDateRange(
                            start, end,
                            DateUiStyle.MD_WITH_WEEKDAY
                        )

                    }
                }
            }
        }
    }

    //면접 여부 버튼 설정
    private fun setupInterviewOptionToggle() {
        binding.interviewYesBtn.setOnClickListener {
            viewModel.updateHasInterview(true)
        }
        binding.interviewNoBtn.setOnClickListener {
            viewModel.updateHasInterview(false)
        }
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
            updateLocation()
            findNavController().popBackStack()
        }

        binding.nextBtn.setOnClickListener {
            updateLocation()
            val action =
                InterviewSettingFragmentDirections.actionInterviewSettingFragmentToRecruitFormSettingFragment()
            findNavController().navigate(action)
        }
    }

    private fun updateLocation() {
        viewModel.updateInterviewLocation(binding.interviewLocInput.text.toString())
    }

    private fun setupBackHandler() {
        // 시스템 뒤로가기(제스처 포함) 가로채기
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    updateLocation()
                    findNavController().popBackStack()

                }
            }
        )

    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)
            ?.setToolbar(detailVisible = true, title = "모임 올리기")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}