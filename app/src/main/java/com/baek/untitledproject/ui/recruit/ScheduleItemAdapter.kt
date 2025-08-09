package com.baek.untitledproject.ui.recruit.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.baek.untitledproject.databinding.ItemScheduleItemBinding
import com.baek.untitledproject.domain.data.ScheduleItemSummary

class ScheduleItemAdapter : ListAdapter<ScheduleItemSummary, ScheduleItemAdapter.ScheduleItemViewHolder>(ScheduleItemDiffCallback) {

    companion object {
        private val ScheduleItemDiffCallback = object : DiffUtil.ItemCallback<ScheduleItemSummary>() {
            override fun areItemsTheSame(
                oldItem: ScheduleItemSummary,
                newItem: ScheduleItemSummary
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: ScheduleItemSummary,
                newItem: ScheduleItemSummary
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class ScheduleItemViewHolder(
        private val binding: ItemScheduleItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ScheduleItemSummary) {
            binding.dateTxt.text = item.date
            binding.timeTxt.text = item.time
            binding.organizationTxt.text = item.organization
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleItemViewHolder {
        val binding = ItemScheduleItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ScheduleItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScheduleItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}