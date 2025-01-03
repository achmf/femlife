package com.example.femlife.ui.fragments.femtalk

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.femlife.data.femtalk.Post
import com.example.femlife.databinding.ItemPostBinding
import com.example.femlife.utils.ImageUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PostAdapter(
    private val onPostClick: (Post) -> Unit,
    private val onCommentClick: (Post) -> Unit,
    private val onMenuClick: (Post, View) -> Unit
) : ListAdapter<Post, PostAdapter.PostViewHolder>(PostDiffCallback()) {

    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }

    fun updatePost(updatedPost: Post) {
        val currentList = currentList.toMutableList()
        val index = currentList.indexOfFirst { it.id == updatedPost.id }
        if (index != -1) {
            currentList[index] = updatedPost
            submitList(currentList)
        }
    }

    inner class PostViewHolder(private val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {
            binding.apply {
                tvUsername.text = if (post.userId == currentUserId) "You" else "FemTalk User"
                tvCaption.text = post.caption

                // Load post image using Glide
                Glide.with(ivPostImage.context)
                    .load(post.imageUrl)
                    .into(ivPostImage)

                // Load user avatar using ImageUtils
                loadUserAvatar(post.userId)

                // Hide the menu if the post doesn't belong to the current user
                ivMenu.visibility = if (post.userId == currentUserId) View.VISIBLE else View.GONE

                root.setOnClickListener { onPostClick(post) }
                btnComment.setOnClickListener { onCommentClick(post) }
                ivMenu.setOnClickListener { view -> onMenuClick(post, view) }
            }
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

    class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
        }
    }
}