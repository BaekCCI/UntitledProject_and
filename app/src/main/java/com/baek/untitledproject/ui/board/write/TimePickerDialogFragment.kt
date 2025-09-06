package com.baek.untitledproject.ui.board.write

import android.app.Dialog
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.NumberPicker
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import com.baek.untitledproject.R
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
        timePicker.setInterval()
        timePicker.setIs24HourView(false)
        timePicker.hour = cur.hour
        timePicker.minute = cur.minute / 5

        binding.completeBtn.setOnClickListener {
            onPicked(LocalTime.of(timePicker.hour, timePicker.minute * 5))
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
        value = 0
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}