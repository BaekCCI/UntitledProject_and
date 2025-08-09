package com.baek.untitledproject.ui.recruit.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.baek.untitledproject.databinding.ItemApplicantListCardBinding
import com.baek.untitledproject.domain.data.ApplicantSummary

class ApplicantListAdapter(
    private val onItemClick: (ApplicantSummary) -> Unit
) : ListAdapter<ApplicantSummary, ApplicantListAdapter.ApplicantListViewHolder>(ApplicantDiffCallback) {

    companion object {
        private val ApplicantDiffCallback = object : DiffUtil.ItemCallback<ApplicantSummary>() {
            override fun areItemsTheSame(
                oldItem: ApplicantSummary,
                newItem: ApplicantSummary
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: ApplicantSummary,
                newItem: ApplicantSummary
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class ApplicantListViewHolder(
        private val binding: ItemApplicantListCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ApplicantSummary) {
            // 기본 정보 설정
            binding.nameTxt.text = item.name
            binding.genderAgeTxt.text = "${item.gender} ${item.age}세"
            binding.departmentTxt.text = item.department
            binding.phoneNumberTxt.text = item.phoneNumber ?: "연락처 없음"

            // 클릭 이벤트 설정
            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApplicantListViewHolder {
        val binding = ItemApplicantListCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ApplicantListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ApplicantListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}