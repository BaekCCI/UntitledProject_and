package com.baek.untitledproject.ui.recruit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.baek.untitledproject.databinding.ItemScheduleGroupBinding
import com.baek.untitledproject.domain.data.ScheduleGroupSummary

class ScheduleGroupAdapter(
    private val onGroupToggle: (String) -> Unit
) : ListAdapter<ScheduleGroupSummary, ScheduleGroupAdapter.ScheduleGroupViewHolder>(ScheduleGroupDiffCallback) {

    companion object {
        private val ScheduleGroupDiffCallback = object : DiffUtil.ItemCallback<ScheduleGroupSummary>() {
            override fun areItemsTheSame(
                oldItem: ScheduleGroupSummary,
                newItem: ScheduleGroupSummary
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: ScheduleGroupSummary,
                newItem: ScheduleGroupSummary
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class ScheduleGroupViewHolder(
        private val binding: ItemScheduleGroupBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val scheduleItemAdapter = ScheduleItemAdapter()

        init {
            // 중첩 RecyclerView 설정
            binding.scheduleItemRecyclerView.apply {
                adapter = scheduleItemAdapter
                layoutManager = LinearLayoutManager(binding.root.context)
                isNestedScrollingEnabled = false
            }
        }

        fun bind(item: ScheduleGroupSummary) {
            // 그룹 제목 설정
            binding.groupTitleTxt.text = "${item.title} ${item.count}건"

            // 펼침/접힘 상태에 따른 UI 업데이트
            updateExpandedState(item.isExpanded)

            // 일정 아이템들 설정
            scheduleItemAdapter.submitList(item.scheduleItems)

            // 헤더 클릭 이벤트
            binding.headerLayout.setOnClickListener {
                onGroupToggle(item.id)
            }
        }

        private fun updateExpandedState(isExpanded: Boolean) {
            if (isExpanded) {
                // 펼침 상태
                binding.scheduleItemRecyclerView.visibility = View.VISIBLE
                binding.divider.visibility = View.VISIBLE
                binding.dropdownIcon.rotation = 180f
            } else {
                // 접힘 상태
                binding.scheduleItemRecyclerView.visibility = View.GONE
                binding.divider.visibility = View.GONE
                binding.dropdownIcon.rotation = 0f
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleGroupViewHolder {
        val binding = ItemScheduleGroupBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ScheduleGroupViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScheduleGroupViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}