package com.example.femlife.ui.fragments.femtalk.create

import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.femlife.R
import com.example.femlife.databinding.ActivityCreateTalkBinding
import com.google.android.material.snackbar.Snackbar

class CreateTalkActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateTalkBinding
    private val viewModel: CreateTalkViewModel by viewModels()
    private var selectedImageUri: Uri? = null

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            Glide.with(this)
                .load(it)
                .into(binding.ivPostImage)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateTalkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupImagePicker()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_post -> {
                    createPost()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupImagePicker() {
        binding.ivPostImage.setOnClickListener {
            getContent.launch("image/*")
        }
    }

    private fun createPost() {
        val caption = binding.etCaption.text.toString().trim()

        if (selectedImageUri == null) {
            Snackbar.make(binding.root, "Please select an image", Snackbar.LENGTH_SHORT).show()
            return
        }

        if (caption.isEmpty()) {
            Snackbar.make(binding.root, "Please write a caption", Snackbar.LENGTH_SHORT).show()
            return
        }

        viewModel.createPost(selectedImageUri!!, caption)
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.topAppBar.menu.findItem(R.id.action_post)?.isEnabled = !isLoading
        }

        viewModel.postCreated.observe(this) { isCreated ->
            if (isCreated) {
                setResult(RESULT_OK)
                finish()
            }
        }

        viewModel.error.observe(this) { errorMessage ->
            Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_LONG).show()
        }
    }
}
