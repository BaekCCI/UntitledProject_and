package com.baek.untitledproject.ui.board.dialogs

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.baek.untitledproject.databinding.ItemImageViewerBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class ImageViewerAdapter(
    private val uris: List<Uri>,
    private val onZoomChanged: (isZoomed: Boolean) -> Unit = {} //zoom 상태 변화 콜백
) : RecyclerView.Adapter<ImageViewerAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemImageViewerBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemImageViewerBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return uris.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val photoView = holder.binding.photoView

        //PhotoView 기본 스케일 레벨 설정
        photoView.minimumScale = 1.0f
        photoView.mediumScale   = 2.5f
        photoView.maximumScale  = 5.0f

        //이미지 로딩
        Glide.with(photoView)
            .load(uris[position])
            .dontTransform()
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(photoView)

        photoView.setOnMatrixChangeListener {
            val isZoomed = photoView.scale > photoView.minimumScale + 0.01f
            onZoomChanged(isZoomed)
        }
    }
    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        val pv = holder.binding.photoView
        // 리스너 해제
        pv.setOnViewTapListener(null)
        pv.setOnMatrixChangeListener(null)
        // 줌 상태 초기화 (재사용 시 이전 줌이 남지 않도록)
        try {
            pv.setScale(pv.minimumScale, false)
        } catch (_: Throwable) { /* 안전 차단 */ }
        // Glide 해제
        Glide.with(pv).clear(pv)
    }


}