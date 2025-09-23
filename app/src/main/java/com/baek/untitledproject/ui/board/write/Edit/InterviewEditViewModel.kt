package com.baek.untitledproject.ui.board.write.Edit

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baek.untitledproject.domain.data.InterviewSlot
import com.baek.untitledproject.domain.data.InterviewTimeSlot
import com.baek.untitledproject.domain.data.TimeSlot
import com.baek.untitledproject.domain.repository.BoardRepository
import com.baek.untitledproject.domain.repository.SessionRepository
import com.baek.untitledproject.domain.repository.UserRepository
import com.baek.untitledproject.domain.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class InterviewEditViewModel @Inject constructor(
    private val boardRepository: BoardRepository,
    private val sessionRepository: SessionRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    //가져온 데이터
    private val _slotState = MutableStateFlow<Result<List<InterviewSlot>>>(Result.Loading)
    val slotState: StateFlow<Result<List<InterviewSlot>>> = _slotState

    //원본 데이터
    private val _dbSlotsByDate = MutableStateFlow<Map<LocalDate, List<InterviewSlot>>>(emptyMap())
    val dbSlotsByDate: StateFlow<Map<LocalDate, List<InterviewSlot>>> = _dbSlotsByDate

    //선택한 날짜
    private val _selectedDate = MutableStateFlow<LocalDate?>(null)
    val selectedDate: StateFlow<LocalDate?> = _selectedDate

    //인터뷰 시간

    private var dbDuration = 30
    private val _duration = MutableStateFlow(30)
    val duration: StateFlow<Int> = _duration

    private val _capacity = MutableStateFlow(1)
    val capacity: StateFlow<Int> = _capacity

    //편집용 데이터
    private val _slotsByDay =
        MutableStateFlow<MutableMap<LocalDate, MutableList<TimeSlot>>>(mutableMapOf())
    val slotsByDay: StateFlow<Map<LocalDate, MutableList<TimeSlot>>> = _slotsByDay

    //예약된 시간들
    private val _reservedSlotsByDate = MutableStateFlow<Map<LocalDate, Set<LocalTime>>>(emptyMap())
    private val reservedSlotsByDate: StateFlow<Map<LocalDate, Set<LocalTime>>> =
        _reservedSlotsByDate

    //ui용 데이터
    val slotItems: StateFlow<List<InterviewTimeSlot>> = slotsByDay.map { buildItem(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    private val _saveState = MutableStateFlow<Result<Unit>>(Result.None)
    val saveState: StateFlow<Result<Unit>> = _saveState

    // ----- 데이터 로드 -----
    fun loadSlots(postId: String) {
        viewModelScope.launch {
            _slotState.value = Result.Loading
            val result = boardRepository.getInterviewSlot(postId)
            Log.d("InterviewEditFragment", "load: $result")
            if (result is Result.Success) {
                val slotByDate = result.data.groupBy { it.interviewDate }
                    .toSortedMap()

                _dbSlotsByDate.value = slotByDate
                _duration.value = slotByDate.values.firstOrNull()?.first()?.duration ?: 30
                dbDuration = duration.value
                _capacity.value = slotByDate.values.firstOrNull()?.first()?.maxCapacity ?: 1

                val merged =
                    slotByDate.mapValues { (_, slots) -> mergeSlots(slots, duration.value) }
                _slotsByDay.value = merged.toSortedMap()

                _reservedSlotsByDate.value = dbSlotsByDate.value.mapValues { (_, s) ->
                    s.filter { it.currentReservations > 0 }.map { it.interviewTime }.toSet()
                }
            }
            _slotState.value = result
            Log.d("InterviewEditFragment", "dbSlotsByDate: ${dbSlotsByDate.value}")
            Log.d("InterviewEditFragment", "reservedSlotsByDate: ${reservedSlotsByDate.value}")
        }
    }

    //date별 TimeSlot 넘겨주기
    private fun mergeSlots(slots: List<InterviewSlot>, step: Int): MutableList<TimeSlot> {
        if (slots.isEmpty()) return mutableListOf()

        val sorted = slots.sortedBy { it.interviewTime }
        val merged = mutableListOf<TimeSlot>()

        var curStart: LocalTime? = null
        var curEnd: LocalTime? = null

        for (slot in sorted) {
            val start = slot.interviewTime
            val end = start.plusMinutes(step.toLong())

            if (curStart == null) {
                curStart = start
                curEnd = end
                continue
            }
            if (curEnd == start) {
                curEnd = end
            } else {
                merged += TimeSlot(curStart, curEnd!!, fromDb = true)
                curStart = start
                curEnd = end
            }
        }
        if (curStart != null && curEnd != null) {
            merged += TimeSlot(curStart, curEnd, fromDb = true)
        }
        return merged
    }

    private fun buildItem(map: Map<LocalDate, List<TimeSlot>>): List<InterviewTimeSlot> {
        if (map.isEmpty()) return emptyList()

        return map
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

    // ---- 사용자 동작 ----

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
        val end = start.plusMinutes(duration.value.toLong())
        _slotsByDay.update { map ->
            val list = (map[day] ?: mutableListOf()).toMutableList()
            list.add(TimeSlot(start, end))
            map.toMutableMap().apply { put(day, list) }
        }
    }

    //----- 슬롯 수정 시 -----

    //----- 슬롯 수정 시 예약 검증 -----

    //시간 범위 -> 시작시간 리스트 추출
    private fun buckets(start: LocalTime, end: LocalTime, stepMin: Int): Set<LocalTime> {
        val out = mutableSetOf<LocalTime>()
        var t = start
        while (t.isBefore(end)) {
            out += t
            t = t.plusMinutes(stepMin.toLong())
        }
        return out
    }

    //수정 슬롯 검사: 예약자가 있는 시간대인지
    private fun validateChangedRange(
        date: LocalDate,
        index: Int,
        newStart: LocalTime? = null,
        newEnd: LocalTime? = null
    ): Boolean {

        if (reservedSlotsByDate.value.isEmpty()) {
            return true
        }
        val prev = _slotsByDay.value[date]?.get(index) ?: return true

        if (!prev.fromDb) return true

        val newS = newStart ?: prev.start
        val newE = newEnd ?: prev.end

        val step = duration.value
        val removed = buckets(prev.start, prev.end, step) - buckets(newS, newE, step)
        if (removed.isEmpty()) return true

        val reservedStarts = reservedSlotsByDate.value[date].orEmpty()

        return removed.none { it in reservedStarts }
    }

    //슬롯 삭제 시
    private fun validateRemoveSlot(date: LocalDate, index: Int): Boolean {
        val reservedStarts = reservedSlotsByDate.value[date].orEmpty()

        if (reservedStarts.isEmpty()) return true

        val prev = _slotsByDay.value[date]?.getOrNull(index) ?: return true

        if (!prev.fromDb) return true

        val times = buckets(prev.start, prev.end, duration.value)

        return times.none { it in reservedStarts }
    }

    //예약 존재 시간대 수정 시 reservedSlotsByDate에서 삭제
    private fun removeReservedSlots(date: LocalDate, times: Collection<LocalTime>) {
        if (times.isEmpty()) return
        _reservedSlotsByDate.update { map ->
            val current = map[date]?.toMutableSet() ?: return@update map
            var changed = false
            for (t in times) {
                if (current.remove(t)) changed = true
            }
            if (!changed) return@update map
            map.toMutableMap().apply {
                if (current.isEmpty()) remove(date) else put(date, current)
            }
        }
    }

    //----- 수정 로직 -----

    //슬롯 삭제
    fun removeSlot(date: LocalDate, index: Int, confirmed: Boolean): Boolean {
        val slot = slotsByDay.value[date]?.getOrNull(index) ?: return true

        if (!confirmed) {
            if (!validateRemoveSlot(date, index)) return false
        } else {
            //사라지는 시간들 삭제
            val step = duration.value
            val timesToRemove = buckets(slot.start, slot.end, step)
            removeReservedSlots(date, timesToRemove)
        }
        _slotsByDay.update { map ->
            val list = map[date]?.toMutableList() ?: return@update map
            if (index in list.indices) list.removeAt(index)
            if (list.isEmpty()) {
                map.toMutableMap().apply { remove(date) }
            } else {
                map.toMutableMap().apply { put(date, list) }
            }
        }
        return true
    }

    //시작 시간 변경
    fun updateSlotStart(
        date: LocalDate,
        index: Int,
        start: LocalTime,
        confirmed: Boolean
    ): Boolean {
        val prev = _slotsByDay.value[date]?.get(index) ?: return true
        val newEnd = alignEndToStartGrid(start, prev.end, duration.value)
        val step = duration.value

        if (!confirmed) {
            if (!validateChangedRange(
                    date = date,
                    index = index,
                    newStart = start,
                    newEnd = newEnd
                )
            ) return false
        } else {
            //사라지는 시간들 삭제
            val oldTimes = buckets(prev.start, prev.end, step)
            val newTimes = buckets(start, newEnd, step)
            val removed = oldTimes - newTimes
            removeReservedSlots(date, removed)
        }
        _slotsByDay.update { map ->
            val list = map[date]?.toMutableList() ?: return@update map

            list[index] = prev.copy(start = start, end = newEnd)
            map.toMutableMap().apply { put(date, list) }
        }
        return true
    }

    //종료 시간 변경
    fun updateSlotEnd(date: LocalDate, index: Int, end: LocalTime, confirmed: Boolean): Boolean {
        val step = duration.value
        if (!confirmed) {
            if (!validateChangedRange(date = date, index = index, newEnd = end)) return false
        } else {
            //사라지는 시간 삭제
            val prev = _slotsByDay.value[date]?.getOrNull(index) ?: return true
            val oldTimes = buckets(prev.start, prev.end, step)
            val newTimes = buckets(prev.start, end, step)
            val removed = oldTimes - newTimes
            removeReservedSlots(date, removed)
        }
        _slotsByDay.update { map ->
            val list = map[date]?.toMutableList() ?: return@update map
            val prev = list.getOrNull(index) ?: return@update map

            list[index] = prev.copy(end = end)
            map.toMutableMap().apply { put(date, list) }
        }
        return true
    }

    // ------ 인터뷰 시간 변경 ------
    private fun validateChangeStep(): Boolean {
        return reservedSlotsByDate.value.isEmpty()
    }

    fun plusInterviewTime(confirmed: Boolean): Boolean {
        if (duration.value >= 30) return true

        if (!confirmed) {
            if (!validateChangeStep()) return false
        } else {
            _reservedSlotsByDate.value = emptyMap()
        }
        _duration.value += 10
        adjustAllSlotEndsToStep(duration.value)
        return true
    }

    fun minusInterviewTime(confirmed: Boolean): Boolean {
        if (duration.value <= 10) return true

        if (!confirmed) {
            if (!validateChangeStep()) return false
        } else {
            _reservedSlotsByDate.value = emptyMap()
        }
        _duration.value -= 10
        adjustAllSlotEndsToStep(duration.value)
        return true
    }

    //----- 인원 수정 -----
    private fun validateChangeCapacity(): Boolean {
        val maxReservations = dbSlotsByDate.value.values
            .flatten()
            .maxOfOrNull { it.currentReservations } ?: 0
        return maxReservations < capacity.value
    }

    fun plusCapacity() {
        _capacity.value++
    }

    fun minusCapacity(confirmed: Boolean): Boolean {
        if (capacity.value <= 1) return true
        val newCapacity = capacity.value - 1

        if (!confirmed) {
            if (!validateChangeCapacity()) return false
        } else {
            if (reservedSlotsByDate.value.isNotEmpty()) {
                _reservedSlotsByDate.update { map ->
                    val newMap = map.toMutableMap()

                    dbSlotsByDate.value.forEach { (date, slots) ->
                        slots.forEach { slot ->
                            if (slot.currentReservations > newCapacity) {

                                val reserved = newMap[date]?.toMutableSet() ?: return@forEach
                                reserved.remove(slot.interviewTime)
                                if (reserved.isEmpty()) {
                                    newMap.remove(date)
                                } else {
                                    newMap[date] = reserved
                                }
                            }
                        }
                    }
                    newMap
                }
            }

        }
        _capacity.value = newCapacity
        return true
    }


    //---- endTime 설정 로직 ----

    //시작시간 변경 시 endTime도 변경
    private fun alignEndToStartGrid(start: LocalTime, curEnd: LocalTime, stepMin: Int): LocalTime {
        val startMin = start.toSecondOfDay() / 60
        val endMin = curEnd.toSecondOfDay() / 60
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

    fun save(postId: String) {
        _saveState.value = Result.Loading

        val toSave = mutableListOf<InterviewSlot>()
        val toDelete = mutableListOf<String>()
        val step = duration.value
        val capacity = capacity.value

        val isDurationChanged = step != dbDuration

        val dbSlot = dbSlotsByDate.value
        val editedSlot = slotsByDay.value

        val allDates: Set<LocalDate> = dbSlot.keys + editedSlot.keys

        for (date in allDates) {

            val dbList: List<InterviewSlot> = dbSlot[date].orEmpty()
            val editList: List<TimeSlot> = editedSlot[date].orEmpty()

            val editByTimes: Set<LocalTime> =
                editList.flatMap { buckets(it.start, it.end, step) }.toSet()

            //duration이 바뀌면 해당 날짜의 DB슬롯 전부 삭제
            if (isDurationChanged) {
                for (origin in dbList) {
                    toDelete += origin.slotId
                }
                //편집 결과에 대해 새로 생성
                for (t in editByTimes) {
                    toSave += InterviewSlot(
                        slotId = "",
                        postId = postId,
                        interviewDate = date,
                        interviewTime = t,
                        maxCapacity = capacity,
                        currentReservations = 0,
                        duration = step
                    )
                }
                continue
            }


            val dbByTimes: Map<LocalTime, InterviewSlot> = dbList.associateBy { it.interviewTime }

            for (t in editByTimes) {
                val existing: InterviewSlot? = dbByTimes[t]

                //db에 이미 존재하는 슬롯
                if (existing != null) {
                    //예약 인원 변경으로 현재 예약 인원보다 작을 경우
                    if (capacity < existing.currentReservations) {
                        toDelete += existing.slotId
                        toSave += InterviewSlot(
                            slotId = "",
                            postId = postId,
                            interviewDate = date,
                            interviewTime = t,
                            maxCapacity = capacity,
                            currentReservations = 0,
                            duration = step
                        )
                    } else {
                        val temp = if (existing.maxCapacity != capacity) {
                            existing.copy(maxCapacity = capacity)
                        } else {
                            existing
                        }
                        toSave += temp
                    }
                } else {
                    //db에 없고 결과에만 있을 경우(새로 생성된 슬롯)
                    toSave += InterviewSlot(
                        slotId = "",
                        postId = postId,
                        interviewDate = date,
                        interviewTime = t,
                        maxCapacity = capacity,
                        currentReservations = 0,
                        duration = step
                    )
                }
            }

            //원본에만 있고 결과엔 없는 시간 -> db에서 삭제
            for (origin in dbList) {
                if (origin.interviewTime !in editByTimes) {
                    toDelete += origin.slotId
                }
            }
        }

        viewModelScope.launch {

            Log.d("InterviewEditViewModel", toSave.toString())
            Log.d("InterviewEditViewModel", toDelete.toString())
            val result = boardRepository.editInterviewSlot(toSave, toDelete)
            Log.d("InterviewEditViewModel", result.toString())
            _saveState.value = result

        }
    }
}