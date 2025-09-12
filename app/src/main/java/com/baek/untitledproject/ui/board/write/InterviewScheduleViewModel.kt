package com.baek.untitledproject.ui.board.write

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baek.untitledproject.domain.data.InterviewTimeSlot
import com.baek.untitledproject.domain.data.PostWrite
import com.baek.untitledproject.domain.data.TimeSlot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class InterviewScheduleViewModel @Inject constructor(

) : ViewModel() {

    //현재 선택한 날짜
    private val _selectedDate = MutableStateFlow<LocalDate?>(null)
    val selectedDate: StateFlow<LocalDate?> = _selectedDate


    private val _slotsByDay = MutableStateFlow<MutableMap<LocalDate, MutableList<TimeSlot>>>(
        mutableMapOf()
    )

    //인터뷰 시간
    private val _interviewStep = MutableStateFlow(30)
    val interviewStep: StateFlow<Int> = _interviewStep

    private val _capacity = MutableStateFlow(1)
    val capacity: StateFlow<Int> = _capacity

    //ui용
    val slotItems: StateFlow<List<InterviewTimeSlot>> = _slotsByDay.map { buildItem(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    fun init(post: PostWrite) {
        val slots = post.interviewSlot
        val step = post.interviewSlotStep
        val capacity = post.maxCapacity
        if (slots.isEmpty()) return
        _slotsByDay.value = slots.mapValues { it.value.toMutableList() }
            .toMutableMap()
        _interviewStep.value = step
        _capacity.value = capacity
    }

    //캘린더 날짜 선택 시 호출
    fun onDateSelected(date: LocalDate) {
        _selectedDate.value = date

        //slotByDay 초기화
        _slotsByDay.update { map ->
            if (!map.containsKey(date)) map[date] = mutableListOf()
            map
        }
    }

    //슬롯 추가
    fun addSlot(date: LocalDate? = null) {
        if (date != null && date != selectedDate.value) {
            _selectedDate.value = date
        }
        val day = selectedDate.value ?: return
        val start = LocalTime.of(8, 0)
        val end = start.plusMinutes(interviewStep.value.toLong())
        _slotsByDay.update { map ->
            val list = (map[day] ?: mutableListOf()).toMutableList()
            list.add(TimeSlot(start, end))
            map.toMutableMap().apply { put(day, list) }
        }
    }

    //슬롯 삭제
    fun removeSlot(date: LocalDate, index: Int) {
        _slotsByDay.update { map ->
            val list = map[date]?.toMutableList() ?: return@update map
            if (index in list.indices) list.removeAt(index)
            if (list.isEmpty()) {
                map.toMutableMap().apply { remove(date) }
            } else {
                map.toMutableMap().apply { put(date, list) }
            }
        }
    }

    //시작 시간 변경
    fun updateSlotStart(date: LocalDate, index: Int, start: LocalTime) {
        _slotsByDay.update { map ->
            val list = map[date]?.toMutableList() ?: return@update map
            val prev = list.getOrNull(index) ?: return@update map

            val newEnd = alignEndToStartGrid(start, prev.end, interviewStep.value)
            list[index] = prev.copy(start = start, end = newEnd)
            Log.d("InterviewScheduleViewModel","updateSlotStart: ${list[index]}")
            map.toMutableMap().apply { put(date, list) }
        }
    }

    fun updateSlotEnd(date: LocalDate, index: Int, end: LocalTime) {
        _slotsByDay.update { map ->
            val list = map[date]?.toMutableList() ?: return@update map
            val prev = list.getOrNull(index) ?: return@update map

            list[index] = prev.copy(end = end)
            map.toMutableMap().apply { put(date, list) }
        }
    }

    fun plusInterviewTime() {
        if (interviewStep.value >= 30) return
        _interviewStep.value += 10
        adjustAllSlotEndsToStep(interviewStep.value)
    }

    fun minusInterviewTime() {
        if (interviewStep.value <= 10) return
        _interviewStep.value -= 10
        adjustAllSlotEndsToStep(interviewStep.value)
    }

    fun plusCapacity() {
        _capacity.value++
    }

    fun minusCapacity() {
        if (capacity.value <= 1) return
        _capacity.value--
    }


    private fun buildItem(map: Map<LocalDate, List<TimeSlot>>): List<InterviewTimeSlot> {
        if (map.isEmpty()) return emptyList()

        return map.toSortedMap()
            .flatMap { (date, slots) ->
                val rows = slots.mapIndexed { idx, slot ->
                    InterviewTimeSlot.SlotRow(
                        date = date,
                        index = idx,
                        start = slot.start,
                        end = slot.end,
                        isLast = idx == slots.lastIndex
                    )
                }
                listOf(InterviewTimeSlot.DateHeader(date)) + rows
            }
    }

    fun getInterviewSlot(): Map<LocalDate, List<TimeSlot>> {
        val src = _slotsByDay.value
        val map = LinkedHashMap<LocalDate, List<TimeSlot>>(src.size)

        for ((date, list) in src) {
            val merged = mergeSlots(list)
            map[date] = merged
        }
        return map
    }

    private fun mergeSlots(slots: List<TimeSlot>): List<TimeSlot> {
        if (slots.isEmpty()) return emptyList()

        val sorted = slots.asSequence()
            .distinctBy { it.start to it.end }
            .sortedWith(compareBy<TimeSlot> { it.start }.thenBy { it.end })
            .toList()
        val merged = mutableListOf<TimeSlot>()
        for (slot in sorted) {
            if (merged.isEmpty()) {
                merged.add(slot)
                continue
            }
            val last = merged.last()

            if (!slot.start.isAfter(last.end)) {
                if (slot.end.isAfter(last.end)) {
                    merged[merged.lastIndex] = last.copy(end = slot.end)
                }
            } else {
                merged.add(slot)
            }
        }
        return merged
    }

    //--endTime 설정 로직

    //시작시간 변경 시 endTime도 변경
    private fun alignEndToStartGrid(start: LocalTime, curEnd: LocalTime, stepMin: Int): LocalTime {
        val startMin = start.toSecondOfDay() / 60
        val endMin   = curEnd.toSecondOfDay() / 60
        if (endMin <= startMin) return start.plusMinutes(stepMin.toLong())

        val delta = endMin - startMin
        val k = delta / stepMin
        val aligned = startMin + k * stepMin
        val alignedTime = LocalTime.ofSecondOfDay(aligned.toLong() * 60)

        return if (!alignedTime.isAfter(start)) start.plusMinutes(stepMin.toLong()) else alignedTime
    }


    //endTime을 interviewtime 배수로 내림 정렬
    private fun floorAlignToStep(time: LocalTime, stepMin: Int): LocalTime {
        val totalMin = time.toSecondOfDay() / 60
        val alignedMin = (totalMin / stepMin) * stepMin
        return LocalTime.ofSecondOfDay(alignedMin.toLong() * 60)
    }


    private fun adjustAllSlotEndsToStep(newStep: Int) {
        if (_slotsByDay.value.isEmpty()) return

        _slotsByDay.update { map ->
            val newMap = map.toMutableMap()
            newMap.keys.forEach { date ->
                val list = (newMap[date] ?: mutableListOf()).toMutableList()
                val updated = list.map { ts ->
                    var newEnd = floorAlignToStep(ts.end, newStep)
                    if (!newEnd.isAfter(ts.start)) {
                        newEnd = ts.start.plusMinutes(newStep.toLong())
                    }
                    ts.copy(end = newEnd)
                }
                newMap[date] = updated.toMutableList()
            }
            newMap
        }
    }


}