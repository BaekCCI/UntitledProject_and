package com.baek.untitledproject.ui.recruit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.baek.untitledproject.databinding.ItemApplicantCardBinding
import com.baek.untitledproject.domain.data.ApplicantSummary

class ApplicantAdapter(
    private val onItemClick: (ApplicantSummary) -> Unit,
    private val onItemLongClick: (ApplicantSummary) -> Unit,
    private val onSelectionChanged: (() -> Unit)? = null // 선택 변경 콜백 추가
) : ListAdapter<ApplicantSummary, ApplicantAdapter.ApplicantViewHolder>(ApplicantDiffCallback) {

    private var isSelectionMode = false
    private val selectedItems = mutableSetOf<String>()

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

    inner class ApplicantViewHolder(
        private val binding: ItemApplicantCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ApplicantSummary) {
            // 기본 정보 설정
            binding.nameTxt.text = item.name
            binding.ageTxt.text = "${item.gender}, ${item.age}세"
            binding.departmentTxt.text = item.department
            binding.statusLinkTxt.text = item.statusText

            // 상태 아이콘 색상 설정
            updateStatusIcon(item.status)

            // 선택 모드에 따른 체크박스 표시/숨김
            binding.selectionCheckbox.visibility = if (isSelectionMode) View.VISIBLE else View.GONE

            // 체크박스 상태 설정
            binding.selectionCheckbox.isChecked = selectedItems.contains(item.id)

            // 선택된 상태에 따른 카드 배경 변경
            updateCardBackground(selectedItems.contains(item.id))

            // 클릭 이벤트 설정
            setupClickEvents(item)
        }

        private fun updateStatusIcon(status: String) {
            val iconRes = when (status) {
                "submitted" -> com.baek.untitledproject.R.drawable.tag_blue
                "interview_scheduled" -> com.baek.untitledproject.R.drawable.tag_green
                "interview_completed" -> com.baek.untitledproject.R.drawable.tag_green
                "passed" -> com.baek.untitledproject.R.drawable.tag_green
                "failed" -> com.baek.untitledproject.R.drawable.tag_blue
                else -> com.baek.untitledproject.R.drawable.tag_blue
            }
            binding.statusIcon.setBackgroundResource(iconRes)
        }

        private fun updateCardBackground(isSelected: Boolean) {
            if (isSelectionMode && isSelected) {
                // 선택된 상태 - 연한 파란색 배경
                binding.root.setCardBackgroundColor(
                    binding.root.context.getColor(android.R.color.holo_blue_light)
                )
                binding.root.alpha = 0.8f
            } else {
                // 기본 상태 - 흰색 배경
                binding.root.setCardBackgroundColor(
                    binding.root.context.getColor(android.R.color.white)
                )
                binding.root.alpha = 1.0f
            }
        }

        private fun setupClickEvents(item: ApplicantSummary) {
            // 일반 클릭
            binding.root.setOnClickListener {
                if (isSelectionMode) {
                    // 선택 모드에서는 체크박스 토글
                    binding.selectionCheckbox.isChecked = !binding.selectionCheckbox.isChecked
                }
                onItemClick(item)
            }

            // 길게 누르기
            binding.root.setOnLongClickListener {
                onItemLongClick(item)
                true
            }

            // 체크박스 클릭 - 콜백 추가
            binding.selectionCheckbox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedItems.add(item.id)
                } else {
                    selectedItems.remove(item.id)
                }
                updateCardBackground(isChecked)
                // 선택 변경 시 콜백 호출
                onSelectionChanged?.invoke()
            }

            // 상태 링크 클릭
            binding.statusLinkTxt.setOnClickListener {
                // TODO: 상태별 상세 화면으로 이동
                when (item.status) {
                    "submitted" -> {
                        // 지원서 상세보기
                    }
                    "interview_scheduled" -> {
                        // 면접 일정 보기
                    }
                    "interview_completed" -> {
                        // 면접 결과 보기
                    }
                    else -> {
                        // 기본 처리
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApplicantViewHolder {
        val binding = ItemApplicantCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ApplicantViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ApplicantViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    // 선택 모드 관련 메서드들
    fun setSelectionMode(enabled: Boolean) {
        isSelectionMode = enabled
        if (!enabled) {
            selectedItems.clear()
        }
        notifyDataSetChanged()
        onSelectionChanged?.invoke() // 선택 변경 알림
    }

    fun toggleSelection(itemId: String) {
        if (selectedItems.contains(itemId)) {
            selectedItems.remove(itemId)
        } else {
            selectedItems.add(itemId)
        }
        notifyDataSetChanged()
        onSelectionChanged?.invoke() // 선택 변경 알림
    }

    fun selectAll() {
        selectedItems.clear()
        currentList.forEach { applicant ->
            selectedItems.add(applicant.id)
        }
        notifyDataSetChanged()
        onSelectionChanged?.invoke() // 선택 변경 알림
    }

    fun clearSelection() {
        selectedItems.clear()
        notifyDataSetChanged()
        onSelectionChanged?.invoke() // 선택 변경 알림
    }

    fun getSelectedIds(): List<String> {
        return selectedItems.toList()
    }

    fun getSelectedCount(): Int {
        return selectedItems.size
    }
}