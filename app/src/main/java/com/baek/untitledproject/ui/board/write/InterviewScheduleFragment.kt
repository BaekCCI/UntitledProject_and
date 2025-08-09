package com.baek.untitledproject.ui.board.write

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.kizitonwose.calendar.core.yearMonth
import java.time.LocalDate

@AndroidEntryPoint
class InterviewScheduleFragment : Fragment() {

    private var _binding: FragmentInterviewScheduleBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BoardWriteViewModel by hiltNavGraphViewModels(R.id.write_board_nav_graph)


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
        setupCalendar()
        setupDayBinder()
        setupCalendarHeader()
    }

    //캘린더 설정
    private fun setupCalendar() {
        val editing = viewModel.editingPost.value
        startDate = editing.recruitmentStart!!
        endDate = editing.recruitmentEnd!!
        startMonth = startDate.yearMonth
        endMonth = endDate.yearMonth
        val calendar = binding.calendarContainer.calendarView
        calendar.setup(
            //TODO: viewModel의 editingPost의 recruitementStart/End로 설정?
            startMonth = startMonth,
            endMonth = endMonth,
            firstDayOfWeek = DayOfWeek.MONDAY
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
                val enabled = data.position == DayPosition.MonthDate && !date.isBefore(startDate) && !date.isAfter(endDate)

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

        // TODO: 배경 적용
        // 저장된 날짜(연보라 원)
        //container.savedBg.visibility = if (savedDates.contains(date)) View.VISIBLE else View.GONE
        // 선택된 날짜(진보라 원)
        //container.selectedBg.visibility = if (selectedDate == date) View.VISIBLE else View.GONE
    }

    //날짜 클릭시 동작 설정
    private fun onDayClicked(day: CalendarDay) {
        val calendarDay = binding.calendarContainer.calendarView

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    inner class DayViewContainer(view: View) : ViewContainer(view) {
        val dayText = view.findViewById<TextView>(R.id.dayText)
        val savedBg = view.findViewById<View>(R.id.savedBg)
        val selectedBg = view.findViewById<View>(R.id.selectedBg)
    }
}