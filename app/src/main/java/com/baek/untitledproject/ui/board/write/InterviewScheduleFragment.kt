package com.baek.untitledproject.ui.board.write

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.baek.untitledproject.R
import com.baek.untitledproject.databinding.FragmentInterviewScheduleBinding
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.ViewContainer
import dagger.hilt.android.AndroidEntryPoint
import java.time.DayOfWeek
import java.time.YearMonth
import androidx.core.graphics.toColorInt
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.baek.untitledproject.ui.MainActivity
import com.kizitonwose.calendar.core.yearMonth
import kotlinx.coroutines.launch
import java.time.LocalDate

@AndroidEntryPoint
class InterviewScheduleFragment : Fragment() {

    private var _binding: FragmentInterviewScheduleBinding? = null
    private val binding get() = _binding!!

    private val boardWriteViewModel: BoardWriteViewModel by hiltNavGraphViewModels(R.id.write_board_nav_graph)
    private val interviewScheduleViewModel: InterviewScheduleViewModel by viewModels()

    private lateinit var startDate: LocalDate
    private lateinit var endDate: LocalDate
    private lateinit var startMonth: YearMonth
    private lateinit var endMonth: YearMonth

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

        //calendar 설정
        setupCalendar()
        setupDayBinder()
        setupCalendarHeader()
        setupToolBarBtn()

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

    //캘린더 설정
    private fun setupCalendar() {
        val editing = boardWriteViewModel.post.value
        startDate = editing.recruitmentStart ?: LocalDate.now()
        endDate = startDate.plusMonths(12)
        startMonth = startDate.yearMonth
        endMonth = endDate.yearMonth
        val calendar = binding.calendarContainer.calendarView
        calendar.setup(
            startMonth = startMonth,
            endMonth = endMonth,
            firstDayOfWeek = DayOfWeek.SUNDAY
        )
        calendar.scrollToMonth(YearMonth.now().coerceIn(startMonth, endMonth))
    }

    //캘린더 날짜 바인딩 설정
    private fun setupDayBinder() {
        val calendar = binding.calendarContainer.calendarView

        calendar.dayBinder = object : MonthDayBinder<DayViewContainer> {

            override fun bind(container: DayViewContainer, data: CalendarDay) {
                val date = data.date
                container.dayText.text = date.dayOfMonth.toString()

                //이번달 날짜와 모집 기간 범위 내만 활성화
                val enabled =
                    data.position == DayPosition.MonthDate && !date.isBefore(startDate) && !date.isAfter(
                        endDate
                    )

                container.view.isEnabled = enabled
                container.view.alpha = if (enabled) 1f else 0.2f

                //커스텀 디자인 적용
                setupCalendarStyle(container, data)

                container.view.setOnClickListener {
                    if (!enabled) return@setOnClickListener
                    onDayClicked(data)
                }
            }

            override fun create(view: View): DayViewContainer {
                return DayViewContainer(view)
            }

        }
    }

    //캘린더 커스텀 디자인 적용
    private fun setupCalendarStyle(container: DayViewContainer, day: CalendarDay) {
        val date = day.date

        // 주말 색
        when (date.dayOfWeek) {
            DayOfWeek.SATURDAY -> container.dayText.setTextColor("#007AFF".toColorInt())
            DayOfWeek.SUNDAY -> container.dayText.setTextColor("#D93D3D".toColorInt())
            else -> container.dayText.setTextColor(requireContext().getColor(R.color.gray_700))
        }

        //배경 적용
        container.today.visibility = if (LocalDate.now() == date) View.VISIBLE else View.GONE
        if (interviewScheduleViewModel.selectedDate.value == date) {
            container.selectedBg.visibility = View.VISIBLE
            container.dayText.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        } else {
            container.selectedBg.visibility = View.GONE
        }
    }

    //날짜 클릭시 동작 설정
    private fun onDayClicked(day: CalendarDay) {
        val calender = binding.calendarContainer.calendarView
        val clicked = day.date

        val old = interviewScheduleViewModel.selectedDate.value

        interviewScheduleViewModel.onDateSelected(clicked)
        if (old != null) calender.notifyDateChanged(old)
        calender.notifyDateChanged(clicked)
        interviewScheduleViewModel.addSlot()

        Log.d("InterviewScheduleFragment", "onDayClicked $day")
    }

    //캘린더 헤더(<0000년 00월>) 설정
    private fun setupCalendarHeader() {
        val calendar = binding.calendarContainer.calendarView

        calendar.monthScrollListener = { month ->
            binding.calendarContainer.monthTitle.text =
                "${month.yearMonth.year}년 ${month.yearMonth.monthValue}월"

            //이동 버튼 설정
            binding.calendarContainer.prevMonthBtn.visibility =
                if (month.yearMonth > startMonth) View.VISIBLE else View.INVISIBLE
            binding.calendarContainer.nextMonthBtn.visibility =
                if (month.yearMonth < endMonth) View.VISIBLE else View.INVISIBLE
        }
        // 이전 달 이동
        binding.calendarContainer.prevMonthBtn.setOnClickListener {
            calendar.findFirstVisibleMonth()?.yearMonth?.let {
                calendar.smoothScrollToMonth(it.minusMonths(1))
            }
        }

        // 다음 달 이동
        binding.calendarContainer.nextMonthBtn.setOnClickListener {
            calendar.findFirstVisibleMonth()?.yearMonth?.let {
                calendar.smoothScrollToMonth(it.plusMonths(1))
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
                interviewScheduleViewModel.interviewTime.value
            )

            findNavController().popBackStack()
        }
    }

    inner class DayViewContainer(view: View) : ViewContainer(view) {
        val dayText = view.findViewById<TextView>(R.id.dayText)
        val today = view.findViewById<View>(R.id.todayBg)
        val selectedBg = view.findViewById<View>(R.id.selectedBg)
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
                interviewScheduleViewModel.interviewTime.collect() { time ->
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
                TimePickerDialogFragment(current) { picked ->
                    Log.d("InterviewScheduleFragment", "선택 시간: $picked")
                    interviewScheduleViewModel.updateSlotStart(date, index, picked)
                }.show(parentFragmentManager, "timePicker")
            },
            //종료 시간 선택
            onEndClick = { date, index, start, current ->
                val step = interviewScheduleViewModel.interviewTime.value
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
