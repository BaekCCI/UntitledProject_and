package com.baek.untitledproject.ui.recruit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.baek.untitledproject.databinding.ItemMyRecruitCardBinding
import com.baek.untitledproject.domain.data.MyRecruitSummary
import com.bumptech.glide.Glide
import java.time.format.DateTimeFormatter
import java.util.Locale

class MyRecruitPagerAdapter(
    private val onInterviewManageClick: (String) -> Unit,
    private val onApplicantManageClick: (String) -> Unit,
    private val onPostManageClick: (String) -> Unit,
    private val onCardClick: (String) -> Unit = {}
) : ListAdapter<MyRecruitSummary, MyRecruitPagerAdapter.MyRecruitViewHolder>(MyRecruitDiffCallback) {

    companion object {
        private val MyRecruitDiffCallback = object : DiffUtil.ItemCallback<MyRecruitSummary>() {
            override fun areItemsTheSame(oldItem: MyRecruitSummary, newItem: MyRecruitSummary) =
                oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: MyRecruitSummary, newItem: MyRecruitSummary) =
                oldItem == newItem
        }
        private val mdFormatter = DateTimeFormatter.ofPattern("M월 d일", Locale.KOREA)
    }

    inner class MyRecruitViewHolder(
        private val binding: ItemMyRecruitCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MyRecruitSummary) {
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
            binding.interviewManageBtn.setOnClickListener { onInterviewManageClick(item.id) }
            binding.applicantManageBtn.setOnClickListener { onApplicantManageClick(item.id) }
            binding.postManageBtn.setOnClickListener { onPostManageClick(item.id) }
            binding.root.setOnClickListener { onCardClick(item.id) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyRecruitViewHolder {
        val binding = ItemMyRecruitCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MyRecruitViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyRecruitViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
