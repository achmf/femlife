package com.example.femlife.ui.fragments.chats

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.femlife.R
import com.example.femlife.data.chat.ChatGroup
import com.example.femlife.databinding.ItemChatListBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatGroupsAdapter(
    private val onChatClicked: (ChatGroup) -> Unit
) : ListAdapter<ChatGroup, ChatGroupsAdapter.ChatViewHolder>(ChatDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ItemChatListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ChatViewHolder(binding, onChatClicked)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ChatViewHolder(
        private val binding: ItemChatListBinding,
        private val onChatClicked: (ChatGroup) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(chat: ChatGroup) {
            binding.apply {
                tvChatName.text = chat.name
                tvLastMessage.text = chat.lastMessage.takeIf { it.isNotEmpty() } ?: "No messages yet"
                tvTimestamp.text = formatTimestamp(chat.lastMessageTimestamp)

                Glide.with(ivChatImage)
                    .load(chat.imageUrl)
                    .placeholder(R.drawable.default_avatar)
                    .error(R.drawable.default_avatar)
                    .circleCrop()
                    .into(ivChatImage)

                root.setOnClickListener { onChatClicked(chat) }
            }
        }

        private fun formatTimestamp(timestamp: Long): String {
            if (timestamp == 0L) return ""

            val now = System.currentTimeMillis()
            val diff = now - timestamp

            return when {
                diff < 60_000 -> "Just now"
                diff < 3600_000 -> "${diff / 60_000}m ago"
                diff < 86400_000 -> "${diff / 3600_000}h ago"
                else -> SimpleDateFormat("dd/MM/yy", Locale.getDefault())
                    .format(Date(timestamp))
            }
        }
    }

    private class ChatDiffCallback : DiffUtil.ItemCallback<ChatGroup>() {
        override fun areItemsTheSame(oldItem: ChatGroup, newItem: ChatGroup): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ChatGroup, newItem: ChatGroup): Boolean {
            return oldItem == newItem
        }
    }
}

