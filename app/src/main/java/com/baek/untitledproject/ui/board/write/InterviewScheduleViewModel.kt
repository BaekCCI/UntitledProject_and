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
    private val _interviewTime = MutableStateFlow(30)
    val interviewTime: StateFlow<Int> = _interviewTime

    private val _capacity = MutableStateFlow(1)
    val capacity: StateFlow<Int> = _capacity

    //ui용
    val slotItems: StateFlow<List<InterviewTimeSlot>> = _slotsByDay.map { buildItem(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    fun init(post:PostWrite){
        val slots = post.interviewSlot
        val step = post.interviewSlotStep
        val capacity = post.maxCapacity
        if(slots.isEmpty()) return
        _slotsByDay.value = slots.mapValues { it.value.toMutableList() }
            .toMutableMap()
        _interviewTime.value = step
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
        if (date!=null &&  date!= selectedDate.value) {
            _selectedDate.value = date
        }
        val day = selectedDate.value ?: return
        val start = LocalTime.of(8, 0)
        val end = start.plusMinutes(interviewTime.value.toLong())
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

            list[index] = prev.copy(start = start)
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
        if (interviewTime.value >= 30) return
        _interviewTime.value += 10
    }

    fun minusInterviewTime() {
        if (interviewTime.value <= 10) return
        _interviewTime.value -= 10
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
                        end = slot.end
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

}