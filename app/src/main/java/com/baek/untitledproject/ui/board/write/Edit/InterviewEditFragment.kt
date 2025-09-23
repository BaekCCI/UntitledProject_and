package com.baek.untitledproject.ui.board.write.Edit

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.baek.untitledproject.R
import com.baek.untitledproject.databinding.FragmentInterviewEditBinding
import com.baek.untitledproject.domain.utils.Result
import com.baek.untitledproject.ui.MainActivity
import com.baek.untitledproject.ui.board.write.ExitConfirmDialogFragment
import com.baek.untitledproject.ui.board.write.InterviewScheduleAdapter
import com.baek.untitledproject.ui.board.write.InterviewToolTipDialogFragment
import com.baek.untitledproject.ui.board.write.common.TimePickerDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate

@AndroidEntryPoint
class InterviewEditFragment : Fragment() {

    private var _binding: FragmentInterviewEditBinding? = null
    private val binding get() = _binding!!

    private val viewModel: InterviewEditViewModel by viewModels()
    private val args: InterviewEditFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInterviewEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        load()
        setupToolTipBtn()
        observeSlotsExists()
        setupInterviewOption()
        observeInterviewTime()
        observeCapacity()

        setupAdapter()
        observeTimeSlot()

        setupToolBarBtn()
        setupBottomAction()
        setupBackPressHandler()
        setupDialogs()

        setupCalendar()

        observeSaveState()
    }

    fun load() {
        val id = args.postId
        viewModel.loadSlots(id)
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
                viewModel.slotItems.collect { slots ->
                    if (slots.isEmpty()) {
                        binding.timeListLayout.visibility = View.GONE
                        binding.emptyTxt.visibility = View.VISIBLE
                        binding.completeBtn.isEnabled = false
                    } else {
                        binding.timeListLayout.visibility = View.VISIBLE
                        binding.emptyTxt.visibility = View.GONE
                        binding.completeBtn.isEnabled = true
                    }
                }
            }
        }
    }

    private fun setupInterviewOption() {
        binding.timeMinusBtn.setOnClickListener {
            val isValid = viewModel.minusInterviewTime(false)
            Log.d("InterviewEditFragment", "timeMinusBtn: $isValid")
            if (!isValid) {
                showConfirmDialog() {
                    viewModel.minusInterviewTime(true)
                }
            }
        }
        binding.timePlusBtn.setOnClickListener {
            val isValid = viewModel.plusInterviewTime(false)
            Log.d("InterviewEditFragment", "timePlusBtn: $isValid")
            if (!isValid) {
                showConfirmDialog() {
                    viewModel.plusInterviewTime(true)
                }
            }
        }

        binding.capacityMinusBtn.setOnClickListener {
            val isValid = viewModel.minusCapacity(false)
            Log.d("InterviewEditFragment", "capacityMinusBtn: $isValid")
            if (!isValid) {
                showConfirmDialog(
                    content = "면접을 예약한 사람이 있어요.\n변경 시 기존 예약은 취소돼요",
                    confirmText = "인원 변경"
                ) {
                    viewModel.minusCapacity(true)
                }
            }
        }
        binding.capacityPlusBtn.setOnClickListener {
            viewModel.plusCapacity()
        }
    }

    private fun observeInterviewTime() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.duration.collect() { time ->
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
                viewModel.capacity.collect() { capacity ->
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

    //-----어댑터 설정 -----
    private fun setupAdapter() {
        adapter = InterviewScheduleAdapter(
            //시작 시간 선택
            onStartClick = { date, index, current ->
                TimePickerDialogFragment(
                    cur = current,
                    step = viewModel.duration.value
                ) { picked ->
                    Log.d("InterviewScheduleFragment", "선택 시간: $picked")
                    val isValid = viewModel.updateSlotStart(date, index, picked, false)
                    Log.d("InterviewEditFragment", "updateSlotStart: $isValid")
                    if (!isValid) {
                        showConfirmDialog() {
                            viewModel.updateSlotStart(date, index, picked, true)
                        }
                    }
                }.show(parentFragmentManager, "timePicker")
            },
            //종료 시간 선택
            onEndClick = { date, index, start, current ->
                val step = viewModel.duration.value
                TimePickerDialogFragment(current, false, start, step) { picked ->
                    Log.d("InterviewScheduleFragment", "선택 시간: $picked")
                    val isValid = viewModel.updateSlotEnd(date, index, picked, false)
                    Log.d("InterviewEditFragment", "updateSlotEnd: $isValid")
                    if (!isValid) {
                        showConfirmDialog() {
                            viewModel.updateSlotEnd(date, index, picked, true)
                        }
                    }
                }.show(parentFragmentManager, "timePicker")
            },
            //delete slot
            onRemoveSlot = { date, index ->
                val isValid = viewModel.removeSlot(date, index, false)
                Log.d("InterviewEditFragment", "removeSlot: $isValid")
                if (!isValid) {
                    showConfirmDialog(
                        content = "해당 시간에 면접을 예약한 사람이 있어요.\n삭제하시겠습니까?",
                        confirmText = "삭제"
                    ) {
                        viewModel.removeSlot(date, index, true)
                    }
                }
            },
            //add slot
            onAddSlot = { date ->
                viewModel.addSlot(date)
            }
        )
        binding.timeList.layoutManager = LinearLayoutManager(requireContext())
        binding.timeList.adapter = adapter
    }

    private fun observeTimeSlot() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.slotItems.collect { items ->
                    Log.d("InterviewScheduleFragment", "$items")
                    adapter.submitList(items)
                }
            }
        }
    }

    //----- 저장 상태 관찰
    private fun observeSaveState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.saveState.collect { state ->
                    when (state) {
                        is Result.Success -> {
                            Toast.makeText(requireContext(), "면접일정이 설정되었습니다", Toast.LENGTH_SHORT)
                                .show()
                            findNavController().popBackStack()
                        }

                        is Result.Loading -> {
                            Log.d("InterviewEditFragment", "로딩중")
                        }

                        is Result.Error -> {

                        }

                        else -> {

                        }
                    }
                }
            }
        }
    }

    //-----버튼 설정 ---

    private fun setupToolBarBtn() {
        binding.cancelBtn.setOnClickListener {
            showExitConfirmDialog()
        }
    }

    private fun setupBottomAction() {
        binding.prevBtn.setOnClickListener {
            showExitConfirmDialog()
        }
        binding.completeBtn.setOnClickListener {
            viewModel.save(args.postId)
        }
    }

    private fun setupCalendar() {
        val startDate = LocalDate.now().minusMonths(12)
        val endDate = startDate.plusMonths(24)

        binding.calendarWidget.init(
            startDate = startDate,
            endDate = endDate,
            firstDayOfWeek = DayOfWeek.SUNDAY
        )

        binding.calendarWidget.setOnDateSelected { date ->
            viewModel.onDateSelected(date)
            viewModel.addSlot()

        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectedDate.collect { selected ->
                    binding.calendarWidget.setSelectedDate(selected)
                }
            }
        }
    }

    // ---------- Exit confirm ----------

    private fun showExitConfirmDialog() {
        ExitConfirmDialogFragment.newInstance(
            content = "수정된 일정은 저장되지 않습니다.\n저장하지 않고 나가시겠어요?",
            continueText = "계속 설정하기"
        ).show(parentFragmentManager, "exit_dialog")
    }

    private fun setupBackPressHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            showExitConfirmDialog()
        }
    }

    private fun setupDialogs() {
        parentFragmentManager.setFragmentResultListener(
            "req_exit",
            viewLifecycleOwner
        ) { _, bundle ->
            if (bundle.getBoolean("confirmed", false)) {
                findNavController().popBackStack()
            }
        }
    }

    //----- 수정/삭제 dialog -----
    private fun showConfirmDialog(
        content: String? = null,
        confirmText: String? = null,
        onConfirm: () -> Unit
    ) {
        // 리스너 먼저 등록
        parentFragmentManager.setFragmentResultListener(
            "req_confirm",
            viewLifecycleOwner
        ) { _, bundle ->
            if (bundle.getBoolean("confirmed", false)) onConfirm()
            parentFragmentManager.clearFragmentResultListener("req_confirm")
        }
        ConfirmEditDialogFragment.newInstance(content, confirmText)
            .show(parentFragmentManager, "confirm_edit_dialog")
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