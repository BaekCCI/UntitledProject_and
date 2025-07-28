package com.baek.untitledproject.ui.board

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.baek.untitledproject.databinding.ItemBoardLayoutBinding
import com.baek.untitledproject.domain.data.BoardSummary

class BoardRVAdapter(private val onItemClick: (BoardSummary) -> Unit) :
    ListAdapter<BoardSummary, BoardRVAdapter.BoardViewHolder>(BoardDiffCallBack) {

    companion object {
        private val BoardDiffCallBack = object : DiffUtil.ItemCallback<BoardSummary>() {
            override fun areItemsTheSame(
                oldItem: BoardSummary,
                newItem: BoardSummary
            ): Boolean {
               return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: BoardSummary,
                newItem: BoardSummary
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class BoardViewHolder(private val binding: ItemBoardLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: BoardSummary) {
            binding.categoryTxt.text = item.category
            binding.titleTxt.text = item.title
            binding.recruitStateTxt.text = item.recruitStatus


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