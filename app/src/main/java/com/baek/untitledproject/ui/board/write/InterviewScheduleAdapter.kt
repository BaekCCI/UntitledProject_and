package com.baek.untitledproject.ui.board.write

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.baek.untitledproject.databinding.ItemInterviewDateHeaderBinding
import com.baek.untitledproject.databinding.ItemInterviewSlotBinding
import com.baek.untitledproject.domain.data.InterviewTimeSlot
import java.time.LocalDate
import java.time.LocalTime

class InterviewScheduleAdapter(
    private val onStartClick: (date: LocalDate, index: Int, current: LocalTime) -> Unit,
    private val onEndClick: (date: LocalDate, index: Int, start: LocalTime, current: LocalTime) -> Unit,
    private val onRemoveSlot: (date: LocalDate, index: Int) -> Unit,
    private val onAddSlot: (date: LocalDate) -> Unit
) : ListAdapter<InterviewTimeSlot, RecyclerView.ViewHolder>(diff) {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_SLOT = 1

        val diff = object : DiffUtil.ItemCallback<InterviewTimeSlot>() {
            override fun areItemsTheSame(
                oldItem: InterviewTimeSlot,
                newItem: InterviewTimeSlot
            ): Boolean =
                when {
                    oldItem is InterviewTimeSlot.DateHeader && newItem is InterviewTimeSlot.DateHeader -> oldItem.date == newItem.date
                    oldItem is InterviewTimeSlot.SlotRow && newItem is InterviewTimeSlot.SlotRow ->
                        oldItem.date == newItem.date && oldItem.index == newItem.index

                    else -> false
                }

            override fun areContentsTheSame(
                oldItem: InterviewTimeSlot,
                newItem: InterviewTimeSlot
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun getItemViewType(position: Int) = when (getItem(position)) {
        is InterviewTimeSlot.DateHeader -> TYPE_HEADER
        is InterviewTimeSlot.SlotRow -> TYPE_SLOT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inf = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_HEADER -> HeaderVH(ItemInterviewDateHeaderBinding.inflate(inf, parent, false))
            else -> SlotVH(ItemInterviewSlotBinding.inflate(inf, parent, false))
        }
    }

    override fun onBindViewHolder(h: RecyclerView.ViewHolder, pos: Int) {
        when (val item = getItem(pos)) {
            is InterviewTimeSlot.DateHeader -> (h as HeaderVH).bind(item.date)
            is InterviewTimeSlot.SlotRow -> (h as SlotVH).bind(
                item,
                onStartClick,
                onEndClick,
                onRemoveSlot,
                onAddSlot
            )
        }
    }

    class HeaderVH(private val b: ItemInterviewDateHeaderBinding) :
        RecyclerView.ViewHolder(b.root) {
        fun bind(date: LocalDate) {
            b.dateTxt.text = date.toString() // format해서 표시
        }
    }

    class SlotVH(private val binding: ItemInterviewSlotBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: InterviewTimeSlot.SlotRow,
            onStart: (LocalDate, Int, LocalTime) -> Unit,
            onEnd: (LocalDate, Int, LocalTime, LocalTime) -> Unit,
            onRemove: (LocalDate, Int) -> Unit,
            onAdd: (LocalDate) -> Unit
        ) {
            binding.startTimeBtn.text = item.start.toString()
            binding.endTimeBtn.text = item.end.toString()

            Log.d("InterviewScheduleAdapter", "${item.date} 생성")

            //클릭 -> 콜백
            binding.startTimeBtn.setOnClickListener {
                onStart(item.date, item.index, item.start)
            }
            binding.endTimeBtn.setOnClickListener {
                onEnd(item.date, item.index, item.start, item.end)
            }
            binding.deleteBtn.setOnClickListener {
                onRemove(item.date, item.index)
            }

            binding.addBtn.setOnClickListener {
                onAdd(item.date)
            }

        }
    }
}