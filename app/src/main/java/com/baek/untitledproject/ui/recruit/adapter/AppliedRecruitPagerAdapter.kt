package com.baek.untitledproject.ui.recruit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.baek.untitledproject.databinding.ItemAppliedRecruitCardBinding
import com.baek.untitledproject.domain.data.AppliedRecruitSummary
import com.bumptech.glide.Glide
import java.time.format.DateTimeFormatter
import java.util.Locale

class AppliedRecruitPagerAdapter(
    private val onInterviewReserveClick: (String) -> Unit,
    private val onViewPostClick: (String) -> Unit,
    private val onCardClick: (String) -> Unit = {}
) : ListAdapter<AppliedRecruitSummary, AppliedRecruitPagerAdapter.AppliedRecruitViewHolder>(AppliedRecruitDiffCallback) {

    companion object {
        private val AppliedRecruitDiffCallback = object : DiffUtil.ItemCallback<AppliedRecruitSummary>() {
            override fun areItemsTheSame(oldItem: AppliedRecruitSummary, newItem: AppliedRecruitSummary) =
                oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: AppliedRecruitSummary, newItem: AppliedRecruitSummary) =
                oldItem == newItem
        }
        private val mdFormatter = DateTimeFormatter.ofPattern("M월 d일", Locale.KOREA)
    }

    inner class AppliedRecruitViewHolder(
        private val binding: ItemAppliedRecruitCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AppliedRecruitSummary) {
            // 기본 정보
            binding.categoryTxt.text = item.category
            binding.titleTxt.text = item.title
            binding.statusTxt.text = item.recruitStatus

            // 썸네일
            if (!item.thumbnailUrl.isNullOrEmpty()) {
                Glide.with(binding.root.context)
                    .load(item.thumbnailUrl)
                    .placeholder(android.R.color.darker_gray)
                    .error(android.R.color.darker_gray)
                    .into(binding.thumbnailImg)
            } else {
                binding.thumbnailImg.setImageResource(android.R.color.darker_gray)
            }

            // 모집 기간: 둘 다 있을 때만 "M월 d일 ~ M월 d일"
            val s = item.recruitmentStart
            val e = item.recruitmentEnd
            if (s != null && e != null) {
                binding.periodRow.visibility = View.VISIBLE
                binding.periodDetailTxt.text = "${s.format(mdFormatter)} ~ ${e.format(mdFormatter)}"
            } else {
                binding.periodRow.visibility = View.GONE
            }

            // 클릭
            binding.interviewReserveBtn.setOnClickListener { onInterviewReserveClick(item.id) }
            binding.viewPostBtn.setOnClickListener { onViewPostClick(item.id) }
            binding.root.setOnClickListener { onCardClick(item.id) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppliedRecruitViewHolder {
        val binding = ItemAppliedRecruitCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return AppliedRecruitViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AppliedRecruitViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
