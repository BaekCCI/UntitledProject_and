package com.baek.untitledproject.ui.board

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.baek.untitledproject.R
import com.baek.untitledproject.databinding.ItemBoardLayoutBinding
import com.baek.untitledproject.databinding.ItemSearchBoardLayoutBinding
import com.baek.untitledproject.domain.data.PostSummary
import com.baek.untitledproject.domain.utils.highlightQuery
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import kotlin.math.roundToInt

class SearchRVAdapter(private val onItemClick: (PostSummary) -> Unit) :
    ListAdapter<PostSummary, RecyclerView.ViewHolder>(SearchDiffCallBack) {

    companion object {
        private const val VT_SEARCHING = 1
        private const val VT_RESULT = 2
        private const val PAYLOAD_HIGHLIGHT = "payload_highlight"

        private val SearchDiffCallBack = object : DiffUtil.ItemCallback<PostSummary>() {
            override fun areItemsTheSame(oldItem: PostSummary, newItem: PostSummary): Boolean {
                return oldItem.postId == newItem.postId
            }

            override fun areContentsTheSame(oldItem: PostSummary, newItem: PostSummary): Boolean {
                return oldItem == newItem
            }
        }
    }

    //검색어 하이라이팅
    private var highlightQuery: String = ""
    fun updateHighlightQuery(q: String) {
        if (highlightQuery == q) return
        highlightQuery = q
        notifyDataSetChanged()
    }

    //검색 중일 때 뷰홀더
    inner class SearchingVH(
        private val binding: ItemSearchBoardLayoutBinding,
        private val onItemClick: (PostSummary) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PostSummary) = with(binding) {

            val color = root.context.getColor(R.color.point_skyblue)
            organizationTxt.text = item.organization.highlightQuery(highlightQuery, color)
            titleTxt.text = item.title.highlightQuery(highlightQuery, color)
            root.setOnClickListener { onItemClick(item) }
        }
    }

    //검색 완료 시 뷰홀더
    inner class ResultVH(
        private val binding: ItemBoardLayoutBinding,
        private val onItemClick: (PostSummary) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        private val radiusPx = (10f * binding.root.resources.displayMetrics.density).roundToInt()

        fun bind(item: PostSummary) = with(binding) {
            organizationTxt.text = item.organization
            titleTxt.text = item.title
            recruitStateTxt.text = if (item.status == "recruiting") "모집 중" else "모집완료"
            Glide.with(binding.thumbnailImg)
                .load(item.imgUri)
                .transform(CenterCrop(), RoundedCorners(radiusPx))
                .into(binding.thumbnailImg)

            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    //검색중/완료에 따라서 SearchFragment에서 호출
    private var isSearching: Boolean = true
    fun setSearchingMode(searching: Boolean) {
        if (isSearching == searching) return
        isSearching = searching
        notifyDataSetChanged()
    }

    //검색중/완료에 따라서 뷰타입 변경
    override fun getItemViewType(position: Int): Int {
        return if (isSearching) VT_SEARCHING else VT_RESULT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inf = LayoutInflater.from(parent.context)
        return when (viewType) {
            VT_SEARCHING -> {
                val b = ItemSearchBoardLayoutBinding.inflate(inf, parent, false)
                SearchingVH(b, onItemClick)
            }

            else -> {
                val b = ItemBoardLayoutBinding.inflate(inf, parent, false)
                ResultVH(b, onItemClick)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is SearchingVH -> holder.bind(item)
            is ResultVH -> holder.bind(item)
        }
    }
}