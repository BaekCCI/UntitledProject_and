package com.baek.untitledproject.ui.board.write

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.baek.untitledproject.R
import com.baek.untitledproject.databinding.FragmentInterviewScheduleBinding
import dagger.hilt.android.AndroidEntryPoint
import java.time.DayOfWeek
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.baek.untitledproject.ui.MainActivity
import kotlinx.coroutines.launch
import java.time.LocalDate

@AndroidEntryPoint
class InterviewScheduleFragment : Fragment() {

    private var _binding: FragmentInterviewScheduleBinding? = null
    private val binding get() = _binding!!

    private val boardWriteViewModel: BoardWriteViewModel by hiltNavGraphViewModels(R.id.write_board_nav_graph)
    private val interviewScheduleViewModel: InterviewScheduleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentInterviewScheduleBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        interviewScheduleViewModel.init(boardWriteViewModel.post.value)

        setupToolBarBtn()
        setupCalendar()
        //인터뷰 설정
        setupToolTipBtn()
        observeSlotsExists()
        setupInterviewOption()
        observeInterviewTime()
        observeCapacity()
        setupAdapter()
        observeTimeSlot()

        setupBottomAction()
    }

    private fun setupCalendar() {
        val startDate = boardWriteViewModel.post.value.recruitmentStart ?: LocalDate.now()
        val endDate = startDate.plusMonths(12)

        binding.calendarWidget.init(
            startDate = startDate,
            endDate = endDate,
            firstDayOfWeek = DayOfWeek.SUNDAY
        )

        binding.calendarWidget.setOnDateSelected { date ->
            interviewScheduleViewModel.onDateSelected(date)
            interviewScheduleViewModel.addSlot()

        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                interviewScheduleViewModel.selectedDate.collect { selected ->
                    binding.calendarWidget.setSelectedDate(selected)
                }
            }
        }
    }


    private fun setupToolBarBtn() {
        binding.cancelBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.doNextBtn.setOnClickListener {

            findNavController().popBackStack()
        }
    }

    private fun setupBottomAction() {
        binding.prevBtn.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.completeBtn.setOnClickListener {
            boardWriteViewModel.updateInterviewSlot(
                interviewScheduleViewModel.getInterviewSlot(),
                interviewScheduleViewModel.capacity.value,
                interviewScheduleViewModel.interviewStep.value
            )

            findNavController().popBackStack()
        }
    }


    //인터뷰 슬롯 설정
    private lateinit var adapter: InterviewScheduleAdapter

    private fun setupToolTipBtn() {
        binding.toolTipBtn.setOnClickListener {
            InterviewToolTipDialogFragment().show(childFragmentManager, "tool_tip")
        }
    }

    private fun observeSlotsExists() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                interviewScheduleViewModel.slotItems.collect { slots ->
                    if (slots.isEmpty()) {
                        binding.timeListLayout.visibility = View.GONE
                        binding.initialLayout.visibility = View.VISIBLE
                        binding.completeBtn.isEnabled = false
                    } else {
                        binding.timeListLayout.visibility = View.VISIBLE
                        binding.initialLayout.visibility = View.GONE
                        binding.completeBtn.isEnabled = true
                    }
                }
            }
        }
    }

    private fun setupInterviewOption() {
        binding.timeMinusBtn.setOnClickListener {
            interviewScheduleViewModel.minusInterviewTime()
        }
        binding.timePlusBtn.setOnClickListener {
            interviewScheduleViewModel.plusInterviewTime()
        }

        binding.capacityMinusBtn.setOnClickListener {
            interviewScheduleViewModel.minusCapacity()
        }
        binding.capacityPlusBtn.setOnClickListener {
            interviewScheduleViewModel.plusCapacity()
        }
    }

    private fun observeInterviewTime() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                interviewScheduleViewModel.interviewStep.collect() { time ->
                    binding.timeTxt.text = time.toString()
                    when (time) {
                        10 -> {
                            binding.timeMinusBtn.imageTintList = ColorStateList.valueOf(
                                ContextCompat.getColor(requireContext(), R.color.gray_300)
                            )
                            binding.timePlusBtn.imageTintList = ColorStateList.valueOf(
                                ContextCompat.getColor(requireContext(), R.color.point_purple)
                            )
                        }

                        30 -> {
                            binding.timeMinusBtn.imageTintList = ColorStateList.valueOf(
                                ContextCompat.getColor(requireContext(), R.color.point_purple)
                            )
                            binding.timePlusBtn.imageTintList = ColorStateList.valueOf(
                                ContextCompat.getColor(requireContext(), R.color.gray_300)
                            )
                        }

                        else -> {
                            binding.timeMinusBtn.imageTintList = ColorStateList.valueOf(
                                ContextCompat.getColor(requireContext(), R.color.point_purple)
                            )

                            binding.timePlusBtn.imageTintList = ColorStateList.valueOf(
                                ContextCompat.getColor(requireContext(), R.color.point_purple)
                            )
                        }
                    }
                }
            }
        }
    }

    private fun observeCapacity() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                interviewScheduleViewModel.capacity.collect() { capacity ->
                    binding.capacityTxt.text = capacity.toString()

                    if (capacity <= 1) {
                        binding.capacityMinusBtn.imageTintList = ColorStateList.valueOf(
                            ContextCompat.getColor(requireContext(), R.color.gray_300)
                        )
                        binding.capacityPlusBtn.imageTintList = ColorStateList.valueOf(
                            ContextCompat.getColor(requireContext(), R.color.point_purple)
                        )
                    } else {
                        binding.capacityMinusBtn.imageTintList = ColorStateList.valueOf(
                            ContextCompat.getColor(requireContext(), R.color.point_purple)
                        )
                        binding.capacityPlusBtn.imageTintList = ColorStateList.valueOf(
                            ContextCompat.getColor(requireContext(), R.color.point_purple)
                        )
                    }
                }
            }
        }
    }

    private fun setupAdapter() {
        adapter = InterviewScheduleAdapter(
            //시작 시간 선택
            onStartClick = { date, index, current ->
                TimePickerDialogFragment(cur= current, step = interviewScheduleViewModel.interviewStep.value) { picked ->
                    Log.d("InterviewScheduleFragment", "선택 시간: $picked")
                    interviewScheduleViewModel.updateSlotStart(date, index, picked)
                }.show(parentFragmentManager, "timePicker")
            },
            //종료 시간 선택
            onEndClick = { date, index, start, current ->
                val step = interviewScheduleViewModel.interviewStep.value
                TimePickerDialogFragment(current, false, start, step) { picked ->
                    Log.d("InterviewScheduleFragment", "선택 시간: $picked")
                    interviewScheduleViewModel.updateSlotEnd(date, index, picked)
                }.show(parentFragmentManager, "timePicker")
            },
            //delete slot
            onRemoveSlot = { date, index ->
                interviewScheduleViewModel.removeSlot(date, index)
            },
            //add slot
            onAddSlot = { date ->
                interviewScheduleViewModel.addSlot(date)
            }
        )
        binding.timeList.layoutManager = LinearLayoutManager(requireContext())
        binding.timeList.adapter = adapter
    }

    private fun observeTimeSlot() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                interviewScheduleViewModel.slotItems.collect { items ->
                    Log.d("InterviewScheduleFragment", "$items")
                    adapter.submitList(items)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)
            ?.setToolbar()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
