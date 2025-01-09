package com.example.femlife.ui.fragments.chats.activity.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.femlife.data.chat.Message
import com.example.femlife.databinding.ItemMessageOtherBinding
import com.example.femlife.databinding.ItemMessageOurBinding
import com.example.femlife.utils.ImageUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MessageAdapter : ListAdapter<Message, RecyclerView.ViewHolder>(MessageDiffCallback()) {

    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    private val firestore = FirebaseFirestore.getInstance()

    companion object {
        private const val VIEW_TYPE_OUR_MESSAGE = 1
        private const val VIEW_TYPE_OTHER_MESSAGE = 2
    }

    override fun getItemViewType(position: Int): Int {
        val message = getItem(position)
        return if (message.senderId == currentUserId) {
            VIEW_TYPE_OUR_MESSAGE
        } else {
            VIEW_TYPE_OTHER_MESSAGE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_OUR_MESSAGE -> {
                val binding = ItemMessageOurBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                OurMessageViewHolder(binding)
            }
            VIEW_TYPE_OTHER_MESSAGE -> {
                val binding = ItemMessageOtherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                OtherMessageViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = getItem(position)
        when (holder) {
            is OurMessageViewHolder -> holder.bind(message)
            is OtherMessageViewHolder -> holder.bind(message)
        }
    }

    inner class OurMessageViewHolder(private val binding: ItemMessageOurBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            binding.tvMessage.text = message.message
            binding.tvTimestamp.text = formatTimestamp(message.timestamp)
        }
    }

    inner class OtherMessageViewHolder(private val binding: ItemMessageOtherBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            binding.tvMessage.text = message.message
            binding.tvTimestamp.text = formatTimestamp(message.timestamp)
            loadUserAvatar(message.senderId)
        }

        private fun loadUserAvatar(userId: String) {
            firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val avatarResId = document.getLong("avatar")?.toInt()
                        ImageUtils.loadAvatar(binding.ivAvatar, avatarResId)
                    }
                }
        }
    }

    private fun formatTimestamp(timestamp: Long): String {
        val messageDate = Date(timestamp)
        val now = Calendar.getInstance()
        val today = Calendar.getInstance().apply { time = messageDate }

        return when {
            now.get(Calendar.DATE) == today.get(Calendar.DATE) &&
                    now.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                    now.get(Calendar.YEAR) == today.get(Calendar.YEAR) -> {
                // If the message is from today, only show the time
                SimpleDateFormat("HH:mm", Locale.getDefault()).format(messageDate)
            }
            now.get(Calendar.YEAR) == today.get(Calendar.YEAR) -> {
                // If the message is from this year, show date and time without year
                SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()).format(messageDate)
            }
            else -> {
                // For older messages, show the full date and time
                dateFormat.format(messageDate)
            }
        }
    }

    private class MessageDiffCallback : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }
    }
}

