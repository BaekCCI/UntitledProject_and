package com.baek.untitledproject.ui.board

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.baek.untitledproject.databinding.ItemImageSliderBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class ImageSliderAdapter(
    private val items: List<Uri>,
    private val onClick: ((position: Int, uri: Uri, imageView: ImageView) -> Unit)? = null
) : RecyclerView.Adapter<ImageSliderAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemImageSliderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(uri: Uri) = with(binding) {
            Glide.with(imageView)
                .load(uri)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .dontTransform()
                .placeholder(android.R.color.transparent) //TODO: 로딩 중 표시할 뷰 설정
                .error(android.R.color.darker_gray) //TODO: 로딩 실패시 표시할 뷰 설정
                .into(imageView)


            root.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    onClick?.invoke(pos, uri, imageView)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemImageSliderBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)

    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

}