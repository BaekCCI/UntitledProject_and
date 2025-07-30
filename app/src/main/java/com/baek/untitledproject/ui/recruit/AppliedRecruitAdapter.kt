package com.baek.untitledproject.ui.recruit.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.baek.untitledproject.databinding.ItemAppliedRecruitCardBinding
import com.baek.untitledproject.domain.data.AppliedRecruitSummary

class AppliedRecruitAdapter(
    private val onInterviewReserveClick: (String) -> Unit,
    private val onViewPostClick: (String) -> Unit,
    private val onCardClick: (String) -> Unit = {}
) : ListAdapter<AppliedRecruitSummary, AppliedRecruitAdapter.AppliedRecruitViewHolder>(AppliedRecruitDiffCallback) {

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
            // 기본 정보 설정
            binding.categoryTxt.text = item.category
            binding.titleTxt.text = item.title

            // 상태 정보 표시 (모집상태 + 지원상태)
            val statusText = "${item.recruitStatus} · ${item.applicationStatus}"
            binding.statusTxt.text = statusText

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