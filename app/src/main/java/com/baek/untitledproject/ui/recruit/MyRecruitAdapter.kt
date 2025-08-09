package com.baek.untitledproject.ui.recruit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.baek.untitledproject.databinding.ItemMyRecruitCardBinding
import com.baek.untitledproject.domain.data.MyRecruitSummary

class MyRecruitAdapter(
    private val onInterviewManageClick: (String) -> Unit,
    private val onApplicantManageClick: (String) -> Unit,
    private val onPostManageClick: (String) -> Unit,
    private val onCardClick: (String) -> Unit = {}
) : ListAdapter<MyRecruitSummary, MyRecruitAdapter.MyRecruitViewHolder>(MyRecruitDiffCallback) {

    companion object {
        private val MyRecruitDiffCallback = object : DiffUtil.ItemCallback<MyRecruitSummary>() {
            override fun areItemsTheSame(
                oldItem: MyRecruitSummary,
                newItem: MyRecruitSummary
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: MyRecruitSummary,
                newItem: MyRecruitSummary
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class MyRecruitViewHolder(
        private val binding: ItemMyRecruitCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MyRecruitSummary) {
            // 기본 정보 설정
            binding.categoryTxt.text = item.category
            binding.titleTxt.text = item.title
            binding.statusTxt.text = item.recruitStatus

            // 썸네일 이미지 설정
            // TODO: Glide 또는 Coil로 이미지 로딩
            // Glide.with(binding.root.context)
            //     .load(item.thumbnailUrl)
            //     .into(binding.thumbnailImg)

            // 버튼 클릭 이벤트
            setupButtonClickEvents(item)

            // 카드 클릭 이벤트
            binding.root.setOnClickListener {
                onCardClick(item.id)
            }
        }

        private fun setupButtonClickEvents(item: MyRecruitSummary) {
            binding.interviewManageBtn.setOnClickListener {
                onInterviewManageClick(item.id)
            }

            binding.applicantManageBtn.setOnClickListener {
                onApplicantManageClick(item.id)
            }

            binding.postManageBtn.setOnClickListener {
                onPostManageClick(item.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyRecruitViewHolder {
        val binding = ItemMyRecruitCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MyRecruitViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyRecruitViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}