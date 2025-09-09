package com.baek.untitledproject.ui.recruit.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.baek.untitledproject.databinding.ItemMyRecruitCardBinding
import com.baek.untitledproject.databinding.ItemEmptyStateCardBinding
import com.baek.untitledproject.domain.data.MyRecruitSummary

class MyRecruitAdapter(
    private val onInterviewManageClick: (String) -> Unit,
    private val onApplicantManageClick: (String) -> Unit,
    private val onPostManageClick: (String) -> Unit,
    private val onCardClick: (String) -> Unit = {}
) : ListAdapter<MyRecruitSummary, RecyclerView.ViewHolder>(MyRecruitDiffCallback) {

    companion object {
        private const val VIEW_TYPE_NORMAL = 0
        private const val VIEW_TYPE_EMPTY = 1

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
                val binding = ItemMyRecruitCardBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                MyRecruitViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is EmptyViewHolder -> {
                holder.bind()
            }
            is MyRecruitViewHolder -> {
                holder.bind(currentList[position])
            }
        }
    }

    inner class EmptyViewHolder(
        private val binding: ItemEmptyStateCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            binding.emptyTitle.text = "올린 공고가 없습니다"
            binding.emptyDescription.text = "새로운 채용 공고를 작성해보세요"
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

            // 인테일 이미지 설정
            // TODO: Glide 또는 Coil로 이미지 로딩

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
}