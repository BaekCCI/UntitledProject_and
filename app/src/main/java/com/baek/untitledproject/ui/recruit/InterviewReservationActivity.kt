package com.baek.untitledproject.ui.recruit

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.baek.untitledproject.databinding.ActivityInterviewReservationBinding
import com.baek.untitledproject.ui.recruit.adapter.InterviewTimeSlotAdapter
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.ViewContainer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@AndroidEntryPoint
class InterviewReservationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInterviewReservationBinding
    private val viewModel: InterviewReservationViewModel by viewModels()

    private lateinit var timeSlotAdapter: InterviewTimeSlotAdapter
    private var selectedDate: LocalDate? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        android.util.Log.d("InterviewReservation", "onCreate() started")
        super.onCreate(savedInstanceState)

        android.util.Log.d("InterviewReservation", "About to inflate layout")
        binding = ActivityInterviewReservationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Intent에서 데이터 받기
        val postId = intent.getStringExtra("postId") ?: run {
            Toast.makeText(this, "공고 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        val applicationId = intent.getStringExtra("applicationId") ?: run {
            Toast.makeText(this, "지원서 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupViews()

        setupCalendar()
        observeViewModel()

        viewModel.initialize(postId, applicationId)

    }

    private fun setupViews() {
        // 뒤로가기 버튼
        binding.backBtn.setOnClickListener {
            finish()
        }

        timeSlotAdapter = InterviewTimeSlotAdapter { slot ->
            if (slot != null) {
                // 시간대 선택됨
                viewModel.selectTimeSlot(slot)
            } else {
                // 시간대 선택 해제됨
                viewModel.clearTimeSlotSelection()
            }
        }

        binding.timeSlotsRecyclerView.apply {
            adapter = timeSlotAdapter
            layoutManager = GridLayoutManager(this@InterviewReservationActivity, 2) // 2열 그리드
        }

        // 예약 버튼
        binding.reserveBtn.setOnClickListener {
            viewModel.reserveInterview()
        }
    }

    private fun setupCalendar() {
        val currentMonth = YearMonth.now()
        val startMonth = currentMonth
        val endMonth = currentMonth.plusMonths(3) // 3개월 후까지
        val daysOfWeek = daysOfWeek()

        binding.calendarContainer.calendarView.setup(startMonth, endMonth, daysOfWeek.first())
        binding.calendarContainer.calendarView.scrollToMonth(currentMonth)

        // 달력 헤더 설정
        updateMonthTitle(currentMonth)

        // 이전/다음 버튼
        binding.calendarContainer.prevMonthBtn.setOnClickListener {
            binding.calendarContainer.calendarView.findFirstVisibleMonth()?.let {
                binding.calendarContainer.calendarView.smoothScrollToMonth(it.yearMonth.minusMonths(1))
            }
        }

        binding.calendarContainer.nextMonthBtn.setOnClickListener {
            binding.calendarContainer.calendarView.findFirstVisibleMonth()?.let {
                binding.calendarContainer.calendarView.smoothScrollToMonth(it.yearMonth.plusMonths(1))
            }
        }

        // 달력 바인더 설정
        binding.calendarContainer.calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.bind(data)
            }
        }

        // 월 변경 리스너
        binding.calendarContainer.calendarView.monthScrollListener = { calendarMonth ->
            updateMonthTitle(calendarMonth.yearMonth)
        }
    }

    private fun updateMonthTitle(yearMonth: YearMonth) {
        val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월", Locale.KOREA)
        binding.calendarContainer.monthTitle.text = yearMonth.format(formatter)
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { state ->
                        handleUiState(state)
                    }
                }

                // 선택된 날짜의 시간대 관찰
                launch {
                    viewModel.selectedDateSlots.collect { slots ->
                        timeSlotAdapter.submitList(slots)
                        timeSlotAdapter.clearSelection()
                    }
                }

                // 전체 시간대 목록이 로드되면 캘린더 새로고침
                launch {
                    viewModel.allInterviewSlots.collect {
                        binding.calendarContainer.calendarView.notifyCalendarChanged()
                    }
                }
            }
        }
    }

    private fun handleUiState(state: InterviewReservationUiState) {

        // 로딩 상태
        binding.loadingLayout.visibility = if (state.isLoading) View.VISIBLE else View.GONE

        // 초기 상태 (날짜 선택 안함)
        binding.initialLayout.visibility = if (!state.showTimeSlots && !state.isLoading) View.VISIBLE else View.GONE

        // 시간대 선택 레이아웃
        binding.timeSelectionLayout.visibility = if (state.showTimeSlots) View.VISIBLE else View.GONE

        // 선택된 날짜 텍스트
        state.selectedDate?.let { date ->
            val formatter = DateTimeFormatter.ofPattern("M월 d일 (E)", Locale.KOREA)
            binding.selectedDateTxt.text = date.format(formatter)
        }

        // 빈 상태 (선택한 날짜에 시간대 없음)
        if (state.showTimeSlots) {
            val hasSlots = viewModel.selectedDateSlots.value.isNotEmpty()
            binding.emptyLayout.visibility = if (!hasSlots) View.VISIBLE else View.GONE
            binding.timeSlotsRecyclerView.visibility = if (hasSlots) View.VISIBLE else View.GONE

        } else {
            binding.emptyLayout.visibility = View.GONE
        }

        // 예약 버튼
        binding.reserveButtonLayout.visibility = if (state.showReserveButton) View.VISIBLE else View.GONE
        binding.reserveBtn.isEnabled = !state.isReserving
        binding.reserveBtn.text = if (state.isReserving) "예약 중..." else "면접 일정 예약하기"

        state.errorMessage?.let { message ->
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }

        // 예약 성공
        if (state.reservationSuccess) {
            Toast.makeText(this, "면접 예약이 완료되었습니다.", Toast.LENGTH_SHORT).show()
            setResult(RESULT_OK)
            finish()
        }
    }

// InterviewReservationActivity.kt의 DayViewContainer 클래스 수정

    inner class DayViewContainer(view: View) : ViewContainer(view) {
        private val dayText = view.findViewById<android.widget.TextView>(com.baek.untitledproject.R.id.dayText)
        private val selectedBg = view.findViewById<View>(com.baek.untitledproject.R.id.selectedBg)
        private val savedBg = view.findViewById<View>(com.baek.untitledproject.R.id.todayBg)

        fun bind(day: CalendarDay) {
            dayText.text = day.date.dayOfMonth.toString()

            val today = LocalDate.now()
            val hasTimeSlots = viewModel.hasTimeSlotsForDate(day.date)
            val isSelected = day.date == selectedDate

            // 요일에 따른 텍스트 색상 결정
            val dayOfWeek = day.date.dayOfWeek.value // 1=월요일, 7=일요일
            val baseTextColor = when (dayOfWeek) {
                6 -> getColor(android.R.color.holo_blue_dark) // 토요일 - 파란색
                7 -> getColor(android.R.color.holo_red_dark)  // 일요일 - 빨간색
                else -> getColor(android.R.color.black)        // 평일 - 검은색
            }

            // 모든 배경 초기화
            selectedBg.visibility = View.GONE
            savedBg.visibility = View.GONE

            when {
                day.position != DayPosition.MonthDate -> {
                    // 다른 달 날짜 - 회색으로 통일
                    dayText.setTextColor(getColor(android.R.color.darker_gray))
                    view.setOnClickListener(null)
                }
                day.date < today -> {
                    // 과거 날짜 - 요일별 색상이지만 연하게
                    val pastColor = when (dayOfWeek) {
                        6 -> getColor(android.R.color.holo_blue_light) // 토요일 - 연한 파란색
                        7 -> getColor(android.R.color.holo_red_light)  // 일요일 - 연한 빨간색
                        else -> getColor(android.R.color.darker_gray)   // 평일 - 회색
                    }
                    dayText.setTextColor(pastColor)
                    view.setOnClickListener(null)
                }
                isSelected -> {
                    // 선택된 날짜 - 흰색 (검은 배경 위에서 보이도록)
                    dayText.setTextColor(getColor(android.R.color.white))
                    selectedBg.visibility = View.VISIBLE
                    setupClickListener(day.date, true)
                }
                hasTimeSlots -> {
                    // 면접 가능한 날짜 - 요일별 색상 적용
                    dayText.setTextColor(baseTextColor)
                    savedBg.visibility = View.VISIBLE
                    setupClickListener(day.date, false)
                }
                else -> {
                    // 면접 없는 날짜 - 요일별 색상이지만 연하게
                    val fadedColor = when (dayOfWeek) {
                        6 -> getColor(android.R.color.holo_blue_light) // 토요일 - 연한 파란색
                        7 -> getColor(android.R.color.holo_red_light)  // 일요일 - 연한 빨간색
                        else -> getColor(android.R.color.darker_gray)   // 평일 - 회색
                    }
                    dayText.setTextColor(fadedColor)
                    view.setOnClickListener(null) // 클릭 불가능
                }
            }
        }

        private fun setupClickListener(date: LocalDate, isCurrentlySelected: Boolean) {
            view.setOnClickListener {
                if (viewModel.hasTimeSlotsForDate(date)) {
                    val previousDate = selectedDate

                    if (isCurrentlySelected) {
                        // 현재 선택된 날짜를 다시 클릭 → 선택 해제
                        selectedDate = null

                        binding.calendarContainer.calendarView.notifyDateChanged(date)
                        viewModel.clearDateSelection()
                    } else {
                        // 새로운 날짜 선택
                        selectedDate = date

                        binding.calendarContainer.calendarView.notifyDateChanged(date)
                        previousDate?.let {
                            binding.calendarContainer.calendarView.notifyDateChanged(it)
                        }

                        viewModel.selectDate(date)
                    }
                }
            }
        }
    }
}