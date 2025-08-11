package com.baek.untitledproject.ui.board.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.baek.untitledproject.R
import com.baek.untitledproject.databinding.ItemNotificationBinding
import com.baek.untitledproject.domain.data.Notification
import com.baek.untitledproject.domain.utils.timeText

class NotificationAdapter() :
    ListAdapter<Notification, NotificationAdapter.ViewHolder>(NotiDiffCallBack) {

    var nowMillis: Long = System.currentTimeMillis()

    companion object {
        private const val PAYLOAD_READ = "PAYLOAD_READ"

        private val NotiDiffCallBack = object : DiffUtil.ItemCallback<Notification>() {
            override fun areItemsTheSame(
                oldItem: Notification,
                newItem: Notification
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: Notification,
                newItem: Notification
            ): Boolean {
                return oldItem == newItem
            }

            override fun getChangePayload(old: Notification, new: Notification): Any? {
                return if (old.isRead != new.isRead) PAYLOAD_READ else null
            }

        }
    }

    inner class ViewHolder(private val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Notification) {
            binding.organizationTxt.text = item.senderOrganization
            binding.timeTxt.text = item.timeText(nowMillis)
            binding.titleTxt.text = item.title
            binding.contentTxt.text = item.message
        }

        fun bindRead(isRead: Boolean) = applyReadBackground(isRead)

        private fun applyReadBackground(isRead: Boolean) = with(binding) {
            val defaultBg = android.graphics.Color.TRANSPARENT
            val unreadBg = ContextCompat.getColor(root.context, R.color.gray_50)

            root.setBackgroundColor(if (isRead) defaultBg else unreadBg)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemNotificationBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.contains(PAYLOAD_READ)) {
            holder.bindRead(getItem(position).isRead)
        } else super.onBindViewHolder(holder, position, payloads)
    }
}