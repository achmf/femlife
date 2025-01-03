package com.example.femlife.ui.activities.article.manager.edit

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.femlife.data.article.Article
import com.example.femlife.databinding.ActivityEditArticleBinding
import com.example.femlife.repository.ArticleRepository
import kotlinx.coroutines.launch

class EditArticleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditArticleBinding
    private lateinit var articleRepository: ArticleRepository
    private var selectedImageUri: Uri? = null
    private lateinit var article: Article

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            binding.imageViewPreview.setImageURI(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        articleRepository = ArticleRepository(this)

        val articleId = intent.getStringExtra("articleId") ?: return finish()

        setupToolbar()
        setupListeners()
        loadArticle(articleId)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Edit Article"

        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupListeners() {
        binding.buttonSelectImage.setOnClickListener {
            getContent.launch("image/*")
        }

        binding.buttonUpdateArticle.setOnClickListener {
            updateArticle()
        }
    }

    private fun loadArticle(articleId: String) {
        lifecycleScope.launch {
            val result = articleRepository.getArticleById(articleId)
            result.onSuccess { loadedArticle ->
                article = loadedArticle
                binding.editTextTitle.setText(article.title)
                binding.editTextDescription.setText(article.description)
                Glide.with(this@EditArticleActivity)
                    .load(article.imageUrl)
                    .into(binding.imageViewPreview)
            }.onFailure { error ->
                Toast.makeText(this@EditArticleActivity, "Error loading article: ${error.message}", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun updateArticle() {
        val title = binding.editTextTitle.text.toString()
        val description = binding.editTextDescription.text.toString()

        if (title.isBlank() || description.isBlank()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val updatedArticle = article.copy(
                    title = title,
                    description = description
                )
                val result = if (selectedImageUri != null) {
                    articleRepository.updateArticleWithImage(updatedArticle, selectedImageUri!!)
                } else {
                    articleRepository.updateArticle(updatedArticle)
                }
                result.onSuccess {
                    Toast.makeText(this@EditArticleActivity, "Article updated successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }.onFailure { error ->
                    Toast.makeText(this@EditArticleActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@EditArticleActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

