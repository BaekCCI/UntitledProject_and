package com.baek.untitledproject.ui.board.write.common

import android.app.Dialog
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.NumberPicker
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import com.baek.untitledproject.databinding.FragmentTimePickerDialogBinding
import java.time.LocalTime
import androidx.core.graphics.drawable.toDrawable

class TimePickerDialogFragment(
    private val cur: LocalTime,
    private val isStart: Boolean = true,
    private val startTime: LocalTime = LocalTime.now(),
    private val step: Int = 30,
    private val onPicked: (LocalTime) -> Unit
) : DialogFragment() {

    private var _binding: FragmentTimePickerDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        _binding = FragmentTimePickerDialogBinding.inflate(layoutInflater)

        if (isStart) {
            setTimePicker()
        } else {
            setNumberPicker()
        }

        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

        binding.cancelBtn.setOnClickListener {
            dismiss()
        }
        return dialog

    }

    private fun setTimePicker() = with(binding) {
        timePicker.visibility = View.VISIBLE
        numberPicker.visibility = View.GONE

        // 12시간 UI 유지 + 5분 간격 표시(스피너 모드일 때 유효)
        timePicker.setIs24HourView(false)
        timePicker.setInterval()

        // 상한: 자정 - step (예: step=30 → 23:30)
        val maxStart = LocalTime.MIDNIGHT.minusMinutes(step.toLong())

        // 초기값을 상한 이내로 클램프
        val init = if (cur.isAfter(maxStart)) maxStart else cur
        timePicker.hour = init.hour
        timePicker.minute = init.minute / 5

        // ── 내부 NumberPicker 참조(제조사별 id 차이 고려) ──
        fun np(idName: String): NumberPicker? {
            val id = Resources.getSystem().getIdentifier(idName, "id", "android")
            return timePicker.findViewById(id)
        }

        val hourPicker = np("hour")
        val minutePicker = np("minute")
        val ampmPicker = np("amPm") ?: np("amPmSpinner") // 일부 OEM용 대체 id

        // ── '돌리고 난 뒤' 판정: 모든 피커가 IDLE이고, 마지막 변경 이후 N ms 경과 ──
        val handler = android.os.Handler(android.os.Looper.getMainLooper())
        var pending: Runnable? = null
        var lastChangeAt = android.os.SystemClock.uptimeMillis()
        var hourScrolling = false
        var minuteScrolling = false
        var ampmScrolling = false
        var internalChange = false

        fun minuteFromPicker(): Int {
            // setInterval 적용으로 minute는 0..11(=0..55 by *5) 인덱스가 일반적이나,
            val raw = minutePicker?.value ?: timePicker.minute
            return if (raw in 0..11) raw * 5 else raw
        }

        fun currentPicked(): LocalTime =
            LocalTime.of(timePicker.hour, minuteFromPicker())

        // 최종 스냅: '사용자가 멈춘 뒤'이고 maxStart 초과면 maxStart로 이동
        fun clampIfNeeded() {
            if (internalChange) return
            val picked = currentPicked()
            if (picked.isAfter(maxStart)) {
                internalChange = true
                timePicker.hour = maxStart.hour
                val idx = maxStart.minute / 5
                timePicker.minute = idx
                minutePicker?.value = idx // OEM 동기화
                internalChange = false
            }
        }

        fun scheduleCheck() {
            pending?.let { handler.removeCallbacks(it) }
            pending = Runnable {
                val idle = !hourScrolling && !minuteScrolling && !ampmScrolling
                val stable = android.os.SystemClock.uptimeMillis() - lastChangeAt >= 180L
                if (idle && stable) clampIfNeeded() else scheduleCheck()
            }
            handler.postDelayed(pending!!, 120L) // 100~200ms 정도가 자연스러움
        }

        // 값이 변할 때: 즉시 스냅하지 않고 '마지막 변경 시각'만 갱신 → 멈춘 뒤에만 처리
        timePicker.setOnTimeChangedListener { _, _, _ ->
            if (internalChange) return@setOnTimeChangedListener
            lastChangeAt = android.os.SystemClock.uptimeMillis()
            scheduleCheck()
        }

        // 스크롤 상태 추적: IDLE일 때만 검사 트리거
        val scrollL = NumberPicker.OnScrollListener { picker, state ->
            val scrolling = state != NumberPicker.OnScrollListener.SCROLL_STATE_IDLE
            when (picker) {
                hourPicker -> hourScrolling = scrolling
                minutePicker -> minuteScrolling = scrolling
                ampmPicker -> ampmScrolling = scrolling
            }
            if (!scrolling) scheduleCheck()
        }
        hourPicker?.setOnScrollListener(scrollL)
        minutePicker?.setOnScrollListener(scrollL)
        ampmPicker?.setOnScrollListener(scrollL)

        // 완료 버튼에서도 최종 안전 클램프
        completeBtn.setOnClickListener {
            clampIfNeeded()
            val picked = currentPicked()
            onPicked(if (picked.isAfter(maxStart)) maxStart else picked)
            dismiss()
        }
    }

    private fun setNumberPicker() = with(binding) {
        timePicker.visibility = View.GONE
        numberPicker.visibility = View.VISIBLE

        val slots = buildSlot()

        numberPicker.setupTimeSlots(slots)

        binding.completeBtn.setOnClickListener {
            val picked = slots[numberPicker.value]
            onPicked(LocalTime.of(picked.hour, picked.minute))
            dismiss()
        }
    }

    private fun buildSlot(): List<LocalTime> {
        val startTotal = startTime.hour * 60 + startTime.minute

        val first = startTotal + step
        val end = 24 * 60

        val result = ArrayList<LocalTime>()
        var t = first
        while (t <= end) {
            result += if (t == end) LocalTime.MIDNIGHT else LocalTime.of(t / 60, t % 60)
            t += step
        }
        return result
    }

    private fun TimePicker.setInterval() {
        val minuteId =
            Resources.getSystem().getIdentifier("minute", "id", "android")

        val minutePicker = findViewById<NumberPicker>(minuteId) ?: return

        val minuteValues = (0 until 60 step 5).map { String.format("%02d", it) }.toTypedArray()

        minutePicker.minValue = 0
        minutePicker.maxValue = 11
        minutePicker.displayedValues = minuteValues

    }

    private fun NumberPicker.setupTimeSlots(slots: List<LocalTime>) {
        val labels = slots.map { it.toString() }.toTypedArray()

        minValue = 0
        maxValue = slots.lastIndex
        displayedValues = labels
        wrapSelectorWheel = false
        value = slots.indexOf(cur)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}