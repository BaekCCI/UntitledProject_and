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
    private val onSelectionChanged: (() -> Unit)? = null
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
            binding.departmentTxt.text = item.department ?: "학과 미정"
            binding.statusLinkTxt.text = item.statusText

            // 상태 아이콘 색상 설정 (Firebase 상태값 기준)
            updateStatusIcon(item.status, item.isPassed)

            // 선택 모드에 따른 체크박스 표시/숨김
            binding.selectionCheckbox.visibility = if (isSelectionMode) View.VISIBLE else View.GONE

            // 체크박스 상태 설정
            binding.selectionCheckbox.isChecked = selectedItems.contains(item.id)

            // 선택된 상태에 따른 카드 배경 변경
            updateCardBackground(selectedItems.contains(item.id))

            // 클릭 이벤트 설정
            setupClickEvents(item)
        }

        private fun updateStatusIcon(status: String, isPassed: Boolean?) {
            val iconRes = when {
                status == "submitted" -> com.baek.untitledproject.R.drawable.tag_blue
                status == "interview_waiting" -> com.baek.untitledproject.R.drawable.tag_green
                status == "review_waiting" -> com.baek.untitledproject.R.drawable.tag_orange
                status == "review_completed" && isPassed == true -> com.baek.untitledproject.R.drawable.tag_green
                status == "review_completed" && isPassed == false -> com.baek.untitledproject.R.drawable.tag_red
                status == "review_completed" && isPassed == null -> com.baek.untitledproject.R.drawable.tag_blue
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
                // Firebase 상태값에 따른 상세 화면 이동
                when (item.status) {
                    "submitted" -> {
                        // 지원서 상세보기
                    }
                    "interview_waiting" -> {
                        // 면접 일정 보기
                    }
                    "review_waiting" -> {
                        // 면접 결과 보기
                    }
                    "review_completed" -> {
                        // 최종 결과 보기
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

    fun updateSelectionByIds(selectedIds: Set<String>) {
        selectedItems.clear()
        selectedItems.addAll(selectedIds)
        notifyDataSetChanged()
    }

    // 선택 모드 관련 메서드들 (기존과 동일)
    fun setSelectionMode(enabled: Boolean) {
        isSelectionMode = enabled
        if (!enabled) {
            selectedItems.clear()
        }
        notifyDataSetChanged()
        onSelectionChanged?.invoke()
    }

    fun toggleSelection(itemId: String) {
        if (selectedItems.contains(itemId)) {
            selectedItems.remove(itemId)
        } else {
            selectedItems.add(itemId)
        }
        notifyDataSetChanged()
        onSelectionChanged?.invoke()
    }

    fun selectAll() {
        selectedItems.clear()
        currentList.forEach { applicant ->
            selectedItems.add(applicant.id)
        }
        notifyDataSetChanged()
        onSelectionChanged?.invoke()
    }

    fun clearSelection() {
        selectedItems.clear()
        notifyDataSetChanged()
        onSelectionChanged?.invoke()
    }

    fun getSelectedIds(): List<String> {
        return selectedItems.toList()
    }

    fun getSelectedCount(): Int {
        return selectedItems.size
    }
}