package com.baek.untitledproject.ui.board.apply

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.baek.untitledproject.databinding.ItemPreviewAnswerBinding
import com.baek.untitledproject.domain.data.QuestionAnswer

class PreviewAdapter() : ListAdapter<QuestionAnswer, PreviewAdapter.ViewHolder>(diff) {

    companion object {
        private const val PAYLOAD_EXPAND = "payload_expand"
        private val diff = object : DiffUtil.ItemCallback<QuestionAnswer>() {
            override fun areItemsTheSame(
                oldItem: QuestionAnswer,
                newItem: QuestionAnswer
            ): Boolean {
                return oldItem.questionId == newItem.questionId
            }

            override fun areContentsTheSame(
                oldItem: QuestionAnswer,
                newItem: QuestionAnswer
            ): Boolean {
                return oldItem == newItem
            }

            override fun getChangePayload(oldItem: QuestionAnswer, newItem: QuestionAnswer): Any? =
                if (oldItem.isExpanded != newItem.isExpanded) PAYLOAD_EXPAND else null
        }
    }

    inner class ViewHolder(private val binding: ItemPreviewAnswerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: QuestionAnswer) {
            binding.question.text = item.questionText
            binding.answer.text = item.answerText
            applyExpanded(item.isExpanded)
            binding.header.setOnClickListener { toggle(bindingAdapterPosition) }
        }

        fun applyExpanded(expended: Boolean) {
            binding.reduceBtn.isVisible = expended
            binding.expendBtn.isVisible = !expended

            binding.answer.maxLines = if (expended) Int.MAX_VALUE else 2
            binding.answer.ellipsize = if (expended) null else TextUtils.TruncateAt.END
        }
    }

    private fun toggle(position: Int) {
        if (position == RecyclerView.NO_POSITION) return
        val current = currentList

        val newList = current.mapIndexed { index, item ->
            if (index == position) item.copy(isExpanded = !item.isExpanded) else item
        }
        submitList(newList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreviewAdapter.ViewHolder {

        val binding =
            ItemPreviewAnswerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PreviewAdapter.ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.any { it == PAYLOAD_EXPAND }) {
            holder.applyExpanded(getItem(position).isExpanded)
        } else {
            onBindViewHolder(holder, position)
        }
    }
}