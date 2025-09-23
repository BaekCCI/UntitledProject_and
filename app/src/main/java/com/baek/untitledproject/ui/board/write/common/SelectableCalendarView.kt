package com.baek.untitledproject.ui.board.write.common

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.core.view.isVisible
import com.baek.untitledproject.R
import com.baek.untitledproject.databinding.ViewCalendarBinding
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.yearMonth
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.ViewContainer
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

class SelectableCalendarView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {

    private val binding = ViewCalendarBinding.inflate(LayoutInflater.from(context), this, true)

    private lateinit var startDate: LocalDate
    private lateinit var endDate: LocalDate
    private lateinit var startMonth: YearMonth
    private lateinit var endMonth: YearMonth

    private var selectedDate: LocalDate? = null
    private var onDateSelected: ((LocalDate) -> Unit)? = null

    fun init(startDate: LocalDate, endDate: LocalDate, firstDayOfWeek: DayOfWeek) {
        this.startDate = startDate
        this.endDate = endDate
        startMonth = startDate.yearMonth
        endMonth = endDate.yearMonth

        with(binding.calendarView) {

            setup(
                startMonth = startMonth,
                endMonth = endMonth,
                firstDayOfWeek = DayOfWeek.SUNDAY
            )
            scrollToMonth(YearMonth.now().coerceIn(startMonth, endMonth))


            dayBinder = object : MonthDayBinder<DayViewContainer> {
                override fun create(view: View) = DayViewContainer(view)

                override fun bind(container: DayViewContainer, data: CalendarDay) {
                    val date = data.date
                    container.dayText.text = date.dayOfMonth.toString()

                    val enabled = data.position == DayPosition.MonthDate
                            && !date.isBefore(startDate)
                            && !date.isAfter(endDate)
                    container.view.isEnabled = enabled
                    container.view.alpha = if (enabled) 1f else 0.2f

                    // 스타일
                    applyStyle(container, date)

                    container.view.setOnClickListener {
                        if (!enabled) return@setOnClickListener
                        val old = selectedDate
                        selectedDate = date
                        if (old != null) notifyDateChanged(old)
                        notifyDateChanged(date)
                        onDateSelected?.invoke(date)
                    }
                }
            }
            monthScrollListener = { month ->
                binding.monthTitle.text = "${month.yearMonth.year}년 ${month.yearMonth.monthValue}월"
                binding.prevMonthBtn.isVisible = month.yearMonth > startMonth
                binding.nextMonthBtn.isVisible = month.yearMonth < endMonth
            }
        }

        binding.prevMonthBtn.setOnClickListener {
            binding.calendarView.findFirstVisibleMonth()?.yearMonth?.let {
                binding.calendarView.smoothScrollToMonth(it.minusMonths(1))
            }
        }
        binding.nextMonthBtn.setOnClickListener {
            binding.calendarView.findFirstVisibleMonth()?.yearMonth?.let {
                binding.calendarView.smoothScrollToMonth(it.plusMonths(1))
            }
        }
    }

    fun setOnDateSelected(listener: (LocalDate) -> Unit) {
        onDateSelected = listener
    }

    fun setSelectedDate(date: LocalDate?) {
        if (selectedDate == date) return
        val old = selectedDate
        selectedDate = date
        old?.let { binding.calendarView.notifyDateChanged(it) }
        date?.let { binding.calendarView.notifyDateChanged(it) }
    }

    private fun applyStyle(container: DayViewContainer, date: LocalDate) {
        when (date.dayOfWeek) {
            DayOfWeek.SATURDAY -> container.dayText.setTextColor("#007AFF".toColorInt())
            DayOfWeek.SUNDAY -> container.dayText.setTextColor("#D93D3D".toColorInt())
            else -> container.dayText.setTextColor(ContextCompat.getColor(context, R.color.gray_700))
        }
        container.today.isVisible = (LocalDate.now() == date)
        if (selectedDate == date) {
            container.selectedBg.isVisible = true
            container.dayText.setTextColor(ContextCompat.getColor(context, R.color.white))
        } else {
            container.selectedBg.isVisible = false
        }
    }

    inner class DayViewContainer(view: View) : ViewContainer(view) {
        val dayText = view.findViewById<TextView>(R.id.dayText)
        val today = view.findViewById<View>(R.id.todayBg)
        val selectedBg = view.findViewById<View>(R.id.selectedBg)
    }

}