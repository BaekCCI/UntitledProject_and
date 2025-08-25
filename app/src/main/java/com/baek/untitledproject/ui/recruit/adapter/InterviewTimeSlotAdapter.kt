package com.baek.untitledproject.ui.recruit.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.baek.untitledproject.R
import com.baek.untitledproject.databinding.ItemInterviewTimeSlotBinding
import com.baek.untitledproject.domain.data.InterviewSlot

class InterviewTimeSlotAdapter(
    private val onTimeSlotClick: (InterviewSlot?) -> Unit  // null이면 선택 해제
) : ListAdapter<InterviewSlot, InterviewTimeSlotAdapter.TimeSlotViewHolder>(TimeSlotDiffCallback) {

    private var selectedSlotId: String? = null

    companion object {
        private val TimeSlotDiffCallback = object : DiffUtil.ItemCallback<InterviewSlot>() {
            override fun areItemsTheSame(oldItem: InterviewSlot, newItem: InterviewSlot): Boolean {
                return oldItem.slotId == newItem.slotId
            }

            override fun areContentsTheSame(oldItem: InterviewSlot, newItem: InterviewSlot): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class TimeSlotViewHolder(
        private val binding: ItemInterviewTimeSlotBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(slot: InterviewSlot) {
            binding.timeSlotBtn.text = slot.timeText
            val isSelected = slot.slotId == selectedSlotId
            updateButtonStyle(isSelected, slot)  // slot 객체 전달

            // 클릭 이벤트 - 예약 가능한 시간만 클릭 가능
            binding.timeSlotBtn.setOnClickListener {
                val isAvailable = slot.currentReservations < slot.maxCapacity
                if (isAvailable) {
                    val previousSelectedId = selectedSlotId

                    if (selectedSlotId == slot.slotId) {
                        selectedSlotId = null
                        notifyItemChanged(bindingAdapterPosition)
                        onTimeSlotClick(null)
                    } else {
                        selectedSlotId = slot.slotId
                        if (previousSelectedId != null) {
                            notifyItemChanged(getPositionById(previousSelectedId))
                        }
                        notifyItemChanged(bindingAdapterPosition)
                        onTimeSlotClick(slot)
                    }
                }
            }
        }

        private fun updateButtonStyle(isSelected: Boolean, slot: InterviewSlot) {
            val btn = binding.timeSlotBtn

            // === Figma 컬러 매핑 ===
            val SKY_BLUE         = Color.parseColor("#5593FF")   // 포인트 컬러 (테두리/텍스트)
            val SKY_BLUE_15_BG   = Color.parseColor("#265593FF") // 선택 배경 (15% 투명)
            val BG_AVAILABLE     = Color.parseColor("#F7F9FC")   // 기본 배경
            val BG_INACTIVE      = Color.parseColor("#E8EDF6")   // 비활성 배경
            val STROKE_DEFAULT   = Color.parseColor("#E1E4E8")   // 기본 테두리
            val TEXT_DEFAULT     = Color.parseColor("#4B5563")   // 기본 텍스트
            val TEXT_INACTIVE    = Color.parseColor("#9AA4B2")   // 비활성 텍스트

            val isFullyBooked = slot.currentReservations >= slot.maxCapacity

            when {
                isSelected -> {
                    // 선택된 상태
                    btn.backgroundTintList = ColorStateList.valueOf(SKY_BLUE_15_BG)
                    btn.strokeColor = ColorStateList.valueOf(SKY_BLUE)
                    btn.setTextColor(SKY_BLUE)
                    btn.isEnabled = true
                }
                !isFullyBooked -> {
                    // 예약 가능 (기본)
                    btn.backgroundTintList = ColorStateList.valueOf(BG_AVAILABLE)
                    btn.strokeColor = ColorStateList.valueOf(STROKE_DEFAULT)
                    btn.setTextColor(TEXT_DEFAULT)
                    btn.isEnabled = true
                }
                else -> {
                    // 예약 마감 (비활성)
                    btn.backgroundTintList = ColorStateList.valueOf(BG_INACTIVE)
                    btn.strokeColor = ColorStateList.valueOf(STROKE_DEFAULT)
                    btn.setTextColor(TEXT_INACTIVE)
                    btn.isEnabled = false
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeSlotViewHolder {
        val binding = ItemInterviewTimeSlotBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TimeSlotViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TimeSlotViewHolder, position: Int) {
        android.util.Log.d("InterviewAdapter", "onBindViewHolder called for position: $position")
        holder.bind(getItem(position))
    }

    /**
     * 선택된 슬롯 가져오기
     */
    fun getSelectedSlot(): InterviewSlot? {
        return selectedSlotId?.let { slotId ->
            currentList.find { it.slotId == slotId }
        }
    }

    /**
     * 선택 상태 초기화
     */
    fun clearSelection() {
        val previousSelectedId = selectedSlotId
        selectedSlotId = null
        if (previousSelectedId != null) {
            notifyItemChanged(getPositionById(previousSelectedId))
        }
    }

    /**
     * ID로 아이템 위치 찾기
     */
    private fun getPositionById(slotId: String): Int {
        return currentList.indexOfFirst { it.slotId == slotId }
    }
}