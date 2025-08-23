package com.baek.untitledproject.ui.recruit.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.baek.untitledproject.databinding.ItemAppliedRecruitCardBinding
import com.baek.untitledproject.domain.data.AppliedRecruitSummary
import com.bumptech.glide.Glide

class AppliedRecruitPagerAdapter(
    private val onInterviewReserveClick: (String) -> Unit,
    private val onViewPostClick: (String) -> Unit,
    private val onCardClick: (String) -> Unit = {}
) : ListAdapter<AppliedRecruitSummary, AppliedRecruitPagerAdapter.AppliedRecruitViewHolder>(AppliedRecruitDiffCallback) {

    companion object {
        private val AppliedRecruitDiffCallback = object : DiffUtil.ItemCallback<AppliedRecruitSummary>() {
            override fun areItemsTheSame(
                oldItem: AppliedRecruitSummary,
                newItem: AppliedRecruitSummary
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: AppliedRecruitSummary,
                newItem: AppliedRecruitSummary
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class AppliedRecruitViewHolder(
        private val binding: ItemAppliedRecruitCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AppliedRecruitSummary) {
            // 썸네일 이미지 설정
            if (!item.thumbnailUrl.isNullOrEmpty()) {
                Glide.with(binding.root.context)
                    .load(item.thumbnailUrl)
                    .placeholder(android.R.color.darker_gray)
                    .error(android.R.color.darker_gray)
                    .into(binding.thumbnailImg)
            } else {
                binding.thumbnailImg.setImageResource(android.R.color.darker_gray)
            }

            // 버튼 클릭 이벤트 설정
            setupButtonClickEvents(item)

            // 카드 클릭 이벤트
            binding.root.setOnClickListener {
                onCardClick(item.id)
            }
        }

        private fun setupButtonClickEvents(item: AppliedRecruitSummary) {
            binding.interviewReserveBtn.setOnClickListener {
                onInterviewReserveClick(item.id)
            }

            binding.viewPostBtn.setOnClickListener {
                onViewPostClick(item.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppliedRecruitViewHolder {
        val binding = ItemAppliedRecruitCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AppliedRecruitViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AppliedRecruitViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}