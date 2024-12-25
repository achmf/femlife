package com.example.femlife.ui.fragments.femtalk.detail

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.femlife.data.femtalk.Post
import com.example.femlife.databinding.ActivityPostDetailBinding
import com.example.femlife.ui.fragments.femtalk.FemTalkViewModel
import com.google.android.material.snackbar.Snackbar

class PostDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostDetailBinding
    private val viewModel: FemTalkViewModel by viewModels()
    private lateinit var commentAdapter: CommentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val postId = intent.getStringExtra("POST_ID") ?: return finish()

        setupToolbar()
        setupCommentRecyclerView()
        setupCommentInput()
        observeViewModel(postId)

        viewModel.getPostDetails(postId)
        viewModel.getComments(postId)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupCommentRecyclerView() {
        commentAdapter = CommentAdapter()
        binding.rvComments.apply {
            layoutManager = LinearLayoutManager(this@PostDetailActivity)
            adapter = commentAdapter
        }
    }

    private fun setupCommentInput() {
        binding.btnSendComment.setOnClickListener {
            val commentText = binding.etComment.text.toString().trim()
            if (commentText.isNotEmpty()) {
                viewModel.addComment(intent.getStringExtra("POST_ID")!!, commentText)
                binding.etComment.text?.clear()
            }
        }
    }

    private fun observeViewModel(postId: String) {
        viewModel.postDetails.observe(this) { post ->
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
    }

    private fun updatePostDetails(post: Post) {
        binding.apply {
            tvUsername.text = post.username
            tvCaption.text = post.caption
            Glide.with(this@PostDetailActivity)
                .load(post.imageUrl)
                .into(ivPostImage)
            btnLike.text = post.likes.toString()
        }
    }
}

