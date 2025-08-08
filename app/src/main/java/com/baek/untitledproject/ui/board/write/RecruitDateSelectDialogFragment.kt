package com.baek.untitledproject.ui.board.write

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.baek.untitledproject.R
import com.baek.untitledproject.databinding.FragmentRecruitDateSelectDialogBinding
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.graphics.drawable.toDrawable
import com.baek.untitledproject.domain.utils.toLocalDate
import com.baek.untitledproject.domain.utils.toStringWithDayOfWeekAndSplitter
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.CompositeDateValidator
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class RecruitDateSelectDialogFragment : DialogFragment() {

    private var _binding: FragmentRecruitDateSelectDialogBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BoardWriteViewModel by hiltNavGraphViewModels(R.id.write_board_nav_graph)

    private var startMillis: Long? = null
    private var endMillis: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecruitDateSelectDialogBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDateBtn()
        setupToolBarBtn()
    }

    private fun setupDateBtn() {
        binding.startDateBtn.setOnClickListener {
            //오늘 이후만 선택 가능하도록
            val todayMillis = MaterialDatePicker.todayInUtcMilliseconds()

            val constraints = CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.from(todayMillis))
                .build()

            //TODO: Style 적용하기 ->.setTheme(R.style.~~)
            val builder = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                .setCalendarConstraints(constraints)

            // 이전에 선택한 날짜 유지
            startMillis?.let { builder.setSelection(it) }

            val picker = builder.build()

            picker.addOnPositiveButtonClickListener { selection ->
                startMillis = selection
                val date = Instant.ofEpochMilli(selection)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                binding.startDateBtn.text = date.toStringWithDayOfWeekAndSplitter()

                // 시작일 변경 시 종료일이 더 앞서면 종료일 초기화
                endMillis?.let { end ->
                    if (end < selection) {
                        endMillis = null
                        binding.endDateBtn.text = ""
                    }
                }
            }

            showPickerSafely("startDatePicker", picker)
        }

        binding.endDateBtn.setOnClickListener {
            // 오늘 이후만 선택 가능
            val todayMillis = MaterialDatePicker.todayInUtcMilliseconds()

            val constraintsBuilder = CalendarConstraints.Builder()

            val validators = mutableListOf<CalendarConstraints.DateValidator>()

            validators.add(DateValidatorPointForward.from(todayMillis))

            // 시작일이 있으면 시작일 이후만 선택 가능
            startMillis?.let { start ->
                validators.add(DateValidatorPointForward.from(start))
            }

            constraintsBuilder.setValidator(
                CompositeDateValidator.allOf(validators)
            )

            //TODO: Style 적용하기 ->.setTheme(R.style.~~)
            val builder = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                .setCalendarConstraints(constraintsBuilder.build())

            // 이전 선택값 유지
            endMillis?.let { builder.setSelection(it) }

            val picker = builder.build()

            picker.addOnPositiveButtonClickListener { selection ->
                endMillis = selection
                val date = Instant.ofEpochMilli(selection)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                binding.endDateBtn.text = date.toStringWithDayOfWeekAndSplitter()
            }

            showPickerSafely("endDatePicker", picker)
        }

    }

    //다이얼로그 중복 방지
    private fun showPickerSafely(tag: String, picker: MaterialDatePicker<Long>) {
        if (parentFragmentManager.findFragmentByTag(tag) == null) {
            picker.show(parentFragmentManager, tag)
        }
    }


    private fun setupToolBarBtn() {
        binding.cancelBtn.setOnClickListener {
            dismiss()
        }
        binding.completeBtn.setOnClickListener {
            val startDate = binding.startDateBtn.text.toString().toLocalDate()
            val endDate = binding.endDateBtn.text.toString().toLocalDate()
            viewModel.updateRecruitPeriod(startDate, endDate)
            dismiss()
        }
    }

    //full-screen 다이얼로그 설정
    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundDrawable(Color.WHITE.toDrawable())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}