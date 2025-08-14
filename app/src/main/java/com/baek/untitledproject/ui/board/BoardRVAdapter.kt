package com.baek.untitledproject.ui.board

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.baek.untitledproject.databinding.ItemBoardLayoutBinding
import com.baek.untitledproject.domain.data.PostSummary

class BoardRVAdapter(private val onItemClick: (PostSummary) -> Unit) :
    ListAdapter<PostSummary, BoardRVAdapter.BoardViewHolder>(BoardDiffCallBack) {

    companion object {
        private val BoardDiffCallBack = object : DiffUtil.ItemCallback<PostSummary>() {
            override fun areItemsTheSame(
                oldItem: PostSummary,
                newItem: PostSummary
            ): Boolean {
               return oldItem.postId == newItem.postId
            }

            override fun areContentsTheSame(
                oldItem: PostSummary,
                newItem: PostSummary
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class BoardViewHolder(private val binding: ItemBoardLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PostSummary) {
            binding.categoryTxt.text = item.organization
            binding.titleTxt.text = item.title
            binding.recruitStateTxt.text = item.status


            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
        val binding = ItemBoardLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return BoardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}