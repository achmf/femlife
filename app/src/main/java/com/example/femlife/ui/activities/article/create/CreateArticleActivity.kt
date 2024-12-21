package com.example.femlife.ui.activities.article.create

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.femlife.databinding.ActivityCreateArticleBinding
import com.example.femlife.repository.ArticleRepository
import kotlinx.coroutines.launch

class CreateArticleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateArticleBinding
    private lateinit var articleRepository: ArticleRepository
    private var selectedImageUri: Uri? = null

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            binding.imageViewPreview.setImageURI(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        articleRepository = ArticleRepository(this)

        setupToolbar()
        setupListeners()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Create Article"

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setupListeners() {
        binding.buttonSelectImage.setOnClickListener {
            getContent.launch("image/*")
        }

        binding.buttonCreateArticle.setOnClickListener {
            createArticle()
        }
    }

    private fun createArticle() {
        val title = binding.editTextTitle.text.toString()
        val description = binding.editTextDescription.text.toString()

        if (title.isBlank() || description.isBlank() || selectedImageUri == null) {
            Toast.makeText(this, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val result = articleRepository.createArticle(title, description, selectedImageUri!!)
                result.onSuccess {
                    Toast.makeText(this@CreateArticleActivity, "Article created successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }.onFailure { error ->
                    Toast.makeText(this@CreateArticleActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@CreateArticleActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

