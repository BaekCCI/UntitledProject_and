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

            // 이미지 개수 표시
            setupImageCount(item)

            // 페이지 인디케이터 설정
            setupPageIndicator(item)

            // 버튼 클릭 이벤트
            setupButtonClickEvents(item)

            // 카드 클릭 이벤트
            binding.root.setOnClickListener {
                onCardClick(item.id)
            }
        }

        private fun setupImageCount(item: MyRecruitSummary) {
            val imageCount = item.imageUrls.size
            if (imageCount > 1) {
                binding.imageCountTxt.visibility = View.VISIBLE
                binding.imageCountTxt.text = "${item.currentImageIndex + 1}/$imageCount"
            } else {
                binding.imageCountTxt.visibility = View.GONE
            }
        }

        private fun setupPageIndicator(item: MyRecruitSummary) {
            val imageCount = item.imageUrls.size
            if (imageCount > 1) {
                binding.pageIndicatorLayout.visibility = View.VISIBLE
                // TODO: 동적으로 점들 생성
                // 현재는 정적으로 2개 점만 있음
            } else {
                binding.pageIndicatorLayout.visibility = View.GONE
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