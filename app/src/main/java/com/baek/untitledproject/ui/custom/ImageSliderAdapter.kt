package com.baek.untitledproject.ui.custom

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.baek.untitledproject.databinding.ItemImageSliderBinding
import com.bumptech.glide.Glide

class ImageSliderAdapter : ListAdapter<String, ImageSliderAdapter.ImageViewHolder>(DiffCallback) {

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class ImageViewHolder(private val binding: ItemImageSliderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        //이미지 URL을 Glide를 이용해 로드
        fun bind(imageUrl: String) {
            Log.d("ImageSliderAdapter","Set Image: $imageUrl")
            Glide.with(binding.root).load(imageUrl).into(binding.imageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding =
            ItemImageSliderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}