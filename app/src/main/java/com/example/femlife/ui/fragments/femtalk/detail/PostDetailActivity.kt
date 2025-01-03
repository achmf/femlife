package com.example.femlife.ui.fragments.femtalk.detail

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.femlife.R
import com.example.femlife.data.femtalk.Comment
import com.example.femlife.data.femtalk.Post
import com.example.femlife.databinding.ActivityPostDetailBinding
import com.example.femlife.ui.fragments.femtalk.FemTalkViewModel
import com.example.femlife.utils.ImageUtils
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore

class PostDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostDetailBinding
    private val viewModel: FemTalkViewModel by viewModels()
    private lateinit var commentAdapter: CommentAdapter
    private lateinit var postId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        postId = intent.getStringExtra("POST_ID") ?: return

        setupToolbar()
        setupCommentRecyclerView()
        setupCommentInput()
        setupLikeButton()
        observeViewModel()

        viewModel.updateIndividualPost(postId)
        viewModel.getComments(postId)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Post Details"

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setupCommentRecyclerView() {
        commentAdapter = CommentAdapter(
            onEditClick = { comment ->
                showEditCommentDialog(comment)
            },
            onDeleteClick = { comment ->
                showDeleteCommentConfirmationDialog(comment)
            }
        )
        binding.rvComments.apply {
            layoutManager = LinearLayoutManager(this@PostDetailActivity)
            adapter = commentAdapter
        }
    }

    private fun setupCommentInput() {
        binding.btnSendComment.setOnClickListener {
            val commentText = binding.etComment.text.toString().trim()
            if (commentText.isNotEmpty()) {
                viewModel.addComment(postId, commentText)
                binding.etComment.text?.clear()
            }
        }
    }

    private fun setupLikeButton() {
        binding.btnLike.setOnClickListener {
            handleLikeClick()
        }
    }

    private fun observeViewModel() {
        viewModel.individualPost.observe(this) { post ->
            updatePostDetails(post)
        }

        viewModel.comments.observe(this) { comments ->
            commentAdapter.submitList(comments)
        }

        viewModel.commentAdded.observe(this) { success ->
            if (success) {
                viewModel.getComments(postId)
            } else {
                Snackbar.make(binding.root, "Failed to add comment", Snackbar.LENGTH_SHORT).show()
            }
        }

        viewModel.likeToggled.observe(this) { success ->
            if (success) {
                viewModel.updateIndividualPost(postId)
            } else {
                Snackbar.make(binding.root, "Failed to update like", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun updatePostDetails(post: Post) {
        binding.apply {
            // Set username based on whether the post belongs to current user
            tvUsername.text = if (post.userId == viewModel.getCurrentUserId()) "You" else "FemTalk User"

            // Load user avatar
            loadUserAvatar(post.userId)

            tvCaption.text = post.caption
            Glide.with(this@PostDetailActivity)
                .load(post.imageUrl)
                .into(ivPostImage)
            updateLikeButton(post)

            // Set menu visibility
            ivMenu.visibility = if (post.userId == viewModel.getCurrentUserId()) View.VISIBLE else View.GONE

            // Set menu click listener
            ivMenu.setOnClickListener { view ->
                showPopupMenu(view, post)
            }
        }
    }

    private fun loadUserAvatar(userId: String) {
        FirebaseFirestore.getInstance().collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val avatarResId = document.getLong("avatar")?.toInt()
                    ImageUtils.loadAvatar(binding.ivUserAvatar, avatarResId)
                }
            }
    }

    private fun updateLikeButton(post: Post) {
        binding.btnLike.apply {
            text = "${post.likes} Likes"
            val isLiked = post.likedBy.contains(viewModel.getCurrentUserId())
            val iconRes = if (isLiked) R.drawable.ic_like_filled else R.drawable.ic_like
            icon = ContextCompat.getDrawable(this@PostDetailActivity, iconRes)
            setTextColor(ContextCompat.getColor(this@PostDetailActivity, if (isLiked) R.color.liked_color else R.color.default_text_color))
        }
    }

    private fun showEditCommentDialog(comment: Comment) {
        val editText = EditText(this).apply {
            setText(comment.text)
        }
        AlertDialog.Builder(this)
            .setTitle("Edit Comment")
            .setView(editText)
            .setPositiveButton("Save") { _, _ ->
                val newText = editText.text.toString()
                viewModel.editComment(postId, comment.id, newText)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteCommentConfirmationDialog(comment: Comment) {
        AlertDialog.Builder(this)
            .setTitle("Delete Comment")
            .setMessage("Are you sure you want to delete this comment?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteComment(postId, comment.id)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showPopupMenu(view: View, post: Post) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.post_menu, popup.menu)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_edit -> {
                    // Handle edit action
                    true
                }
                R.id.action_delete -> {
                    // Handle delete action
                    true
                }
                else -> false
            }
        }

        popup.show()
    }

    private fun handleLikeClick() {
        viewModel.toggleLike(postId)
    }
}

