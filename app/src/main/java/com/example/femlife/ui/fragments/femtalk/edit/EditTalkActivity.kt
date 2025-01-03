package com.example.femlife.ui.fragments.femtalk.edit

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.femlife.R
import com.example.femlife.databinding.ActivityEditTalkBinding
import com.google.android.material.snackbar.Snackbar

class EditTalkActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditTalkBinding
    private val viewModel: EditTalkViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditTalkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val postId = intent.getStringExtra("POST_ID") ?: run {
            finish()
            return
        }

        setupToolbar()
        loadPostDetails(postId)
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_save -> {
                    saveChanges()
                    true
                }
                else -> false
            }
        }
    }

    private fun loadPostDetails(postId: String) {
        viewModel.loadPostDetails(postId)
    }

    private fun saveChanges() {
        val caption = binding.etCaption.text.toString().trim()
        if (caption.isEmpty()) {
            Snackbar.make(binding.root, "Please write a caption", Snackbar.LENGTH_SHORT).show()
            return
        }
        viewModel.updatePost(caption)
    }

    private fun observeViewModel() {
        viewModel.post.observe(this) { post ->
            binding.etCaption.setText(post.caption)
            Glide.with(this)
                .load(post.imageUrl)
                .into(binding.ivPostImage)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.topAppBar.menu.findItem(R.id.action_save)?.isEnabled = !isLoading
        }

        viewModel.postUpdated.observe(this) { isUpdated ->
            if (isUpdated) {
                setResult(RESULT_OK)
                finish()
            }
        }

        viewModel.error.observe(this) { errorMessage ->
            Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_LONG).show()
        }
    }
}

