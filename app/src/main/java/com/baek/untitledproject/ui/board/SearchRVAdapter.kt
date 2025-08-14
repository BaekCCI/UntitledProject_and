package com.baek.untitledproject.ui.board

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.baek.untitledproject.databinding.ItemSearchBoardLayoutBinding
import com.baek.untitledproject.domain.data.PostSummary

class SearchRVAdapter(private val onItemClick: (PostSummary) -> Unit) :
    ListAdapter<PostSummary, SearchRVAdapter.SearchViewHolder>(SearchDiffCallBack) {

    companion object {
        private val SearchDiffCallBack = object : DiffUtil.ItemCallback<PostSummary>() {
            override fun areItemsTheSame(oldItem: PostSummary, newItem: PostSummary): Boolean {
                return oldItem.postId == newItem.postId
            }

            override fun areContentsTheSame(oldItem: PostSummary, newItem: PostSummary): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class SearchViewHolder(private val binding: ItemSearchBoardLayoutBinding) :
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding =
            ItemSearchBoardLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}