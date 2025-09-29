package com.baek.untitledproject.ui.setting.block

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.baek.untitledproject.databinding.ItemBlockBinding
import com.baek.untitledproject.domain.data.Block

class BlockRvAdapter(
    private val onCancelClick: (String) -> Unit
) : ListAdapter<Block, BlockRvAdapter.ViewHolder>(DiffCallback) {

    companion object{
        val DiffCallback = object : DiffUtil.ItemCallback<Block>(){
            override fun areItemsTheSame(oldItem: Block, newItem: Block): Boolean {
                return oldItem.blockId == newItem.blockId
            }

            override fun areContentsTheSame(oldItem: Block, newItem: Block): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class ViewHolder(private val binding: ItemBlockBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Block) {
            binding.nameTxt.text = item.blockedUserName

            binding.cancelBtn.setOnClickListener {
                onCancelClick(item.blockId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       val binding = ItemBlockBinding.inflate(
           LayoutInflater.from(parent.context), parent,false
       )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}