package com.baek.untitledproject.ui.board

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.baek.untitledproject.databinding.ItemSearchBoardLayoutBinding
import com.baek.untitledproject.domain.data.BoardSummary

class SearchRVAdapter(private val onItemClick: (BoardSummary) -> Unit) :
    ListAdapter<BoardSummary, SearchRVAdapter.SearchViewHolder>(SearchDiffCallBack) {

    companion object {
        private val SearchDiffCallBack = object : DiffUtil.ItemCallback<BoardSummary>() {
            override fun areItemsTheSame(oldItem: BoardSummary, newItem: BoardSummary): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: BoardSummary, newItem: BoardSummary): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class SearchViewHolder(private val binding: ItemSearchBoardLayoutBinding) :
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding =
            ItemSearchBoardLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}