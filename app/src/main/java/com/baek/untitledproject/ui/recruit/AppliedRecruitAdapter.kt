package com.baek.untitledproject.ui.recruit.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.baek.untitledproject.databinding.ItemAppliedRecruitCardBinding
import com.baek.untitledproject.databinding.ItemEmptyStateCardBinding
import com.baek.untitledproject.domain.data.AppliedRecruitSummary

class AppliedRecruitAdapter(
    private val onInterviewReserveClick: (String) -> Unit,
    private val onViewPostClick: (String) -> Unit,
    private val onCardClick: (String) -> Unit = {}
) : ListAdapter<AppliedRecruitSummary, RecyclerView.ViewHolder>(AppliedRecruitDiffCallback) {

    companion object {
        private const val VIEW_TYPE_NORMAL = 0
        private const val VIEW_TYPE_EMPTY = 1

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

    override fun getItemViewType(position: Int): Int {
        return if (currentList.isEmpty()) {
            VIEW_TYPE_EMPTY
        } else {
            VIEW_TYPE_NORMAL
        }
    }

    override fun getItemCount(): Int {
        return if (currentList.isEmpty()) 1 else currentList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_EMPTY -> {
                val binding = ItemEmptyStateCardBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                EmptyViewHolder(binding)
            }
            else -> {
                val binding = ItemAppliedRecruitCardBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                AppliedRecruitViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is EmptyViewHolder -> {
                holder.bind()
            }
            is AppliedRecruitViewHolder -> {
                holder.bind(currentList[position])
            }
        }
    }

    inner class EmptyViewHolder(
        private val binding: ItemEmptyStateCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            binding.emptyTitle.text = "지원한 공고가 없습니다"
            binding.emptyDescription.text = "관심있는 공고에 지원해보세요"
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
}