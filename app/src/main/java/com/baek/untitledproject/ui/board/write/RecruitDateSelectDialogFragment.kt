package com.baek.untitledproject.ui.board.write

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.baek.untitledproject.R
import com.baek.untitledproject.databinding.FragmentRecruitDateSelectDialogBinding
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.baek.untitledproject.domain.utils.DateUiStyle
import com.baek.untitledproject.domain.utils.toUiString
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.CompositeDateValidator
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class RecruitDateSelectDialogFragment : DialogFragment() {

    private var _binding: FragmentRecruitDateSelectDialogBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BoardWriteViewModel by hiltNavGraphViewModels(R.id.write_board_nav_graph)

    private var startDate: LocalDate? = null
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
        initField()
        setupDateBtn()
        setupToolBarBtn()
    }

    //이전 선택값이 있으면 해당 날짜로 초기화
    private fun initField() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.editingPost.collect { post ->
                    startDate = post.recruitmentStart ?: LocalDate.now()
                    binding.startDate.text = startDate!!.toUiString(DateUiStyle.YMD_WITH_WEEKDAY)

                    post.recruitmentEnd?.let {
                        binding.endDateBtn.text = it.toUiString(DateUiStyle.YMD_WITH_WEEKDAY)
                        endMillis =
                            it.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                    }
                }
            }
        }
    }

    private fun setupDateBtn() {
        binding.endDateBtn.setOnClickListener {
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
            endMillis?.let { builder.setSelection(it) }

            val picker = builder.build()

            picker.addOnPositiveButtonClickListener { selection ->
                endMillis = selection
                val date = Instant.ofEpochMilli(selection)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                binding.endDateBtn.text = date.toUiString(DateUiStyle.YMD_WITH_WEEKDAY)
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
            val e = endMillis
            if (e == null) {
                Toast.makeText(requireContext(), "종료 날짜를 설정해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val endDate = Instant.ofEpochMilli(e).atZone(ZoneId.systemDefault()).toLocalDate()
            viewModel.updateRecruitPeriod(startDate!!, endDate)
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