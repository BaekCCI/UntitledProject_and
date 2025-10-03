package com.baek.untitledproject.ui.board

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.baek.untitledproject.databinding.ItemBoardLayoutBinding
import com.baek.untitledproject.domain.data.PostSummary
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import kotlin.math.roundToInt

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
            val radiusPx = (10f * binding.root.resources.displayMetrics.density).roundToInt()
            binding.organizationTxt.text = item.organization
            binding.titleTxt.text = item.title
            binding.recruitStateTxt.text = if (item.status == "recruiting") "모집 중" else "모집완료"
            Glide.with(binding.thumbnailImg)
                .load(item.imgUri)
                .transform(CenterCrop(), RoundedCorners(radiusPx))
                .into(binding.thumbnailImg)

            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
        val binding =
            ItemBoardLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BoardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}