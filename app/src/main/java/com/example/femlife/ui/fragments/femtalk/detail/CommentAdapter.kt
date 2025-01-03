package com.example.femlife.ui.fragments.femtalk.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.femlife.data.femtalk.Comment
import com.example.femlife.databinding.ItemCommentBinding
import android.widget.PopupMenu
import com.google.firebase.auth.FirebaseAuth
import com.example.femlife.R
import com.example.femlife.utils.ImageUtils
import com.google.firebase.firestore.FirebaseFirestore

class CommentAdapter(
    private val onEditClick: (Comment) -> Unit,
    private val onDeleteClick: (Comment) -> Unit
) : ListAdapter<Comment, CommentAdapter.CommentViewHolder>(CommentDiffCallback()) {

    private val firestore = FirebaseFirestore.getInstance()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CommentViewHolder(private val binding: ItemCommentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: Comment) {
            binding.apply {
                tvComment.text = comment.text
                tvUsername.text = if (comment.userId == currentUserId) "You" else "FemTalk User"

                // Load user avatar
                loadUserAvatar(comment.userId)

                // Hide the menu button if the comment doesn't belong to the current user
                btnMenu.visibility = if (comment.userId == currentUserId) View.VISIBLE else View.GONE

                btnMenu.setOnClickListener { showPopupMenu(comment) }
            }
        }

        private fun loadUserAvatar(userId: String) {
            firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val avatarResId = document.getLong("avatar")?.toInt()
                        ImageUtils.loadAvatar(binding.ivCommentAvatar, avatarResId)
                    }
                }
        }

        private fun showPopupMenu(comment: Comment) {
            val popupMenu = PopupMenu(binding.root.context, binding.btnMenu)
            popupMenu.menuInflater.inflate(R.menu.menu_comment, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_edit -> {
                        onEditClick(comment)
                        true
                    }
                    R.id.action_delete -> {
                        onDeleteClick(comment)
                        true
                    }
                    else -> false
                }
            }

            popupMenu.show()
        }
    }

    class CommentDiffCallback : DiffUtil.ItemCallback<Comment>() {
        override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem == newItem
        }
    }
}

