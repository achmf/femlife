package com.example.femlife.ui.activities.article.manager.edit

import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.femlife.data.article.Article
import com.example.femlife.data.article.ContentItem
import com.example.femlife.databinding.ActivityEditArticleBinding
import com.example.femlife.repository.ArticleRepository
import kotlinx.coroutines.launch

class EditArticleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditArticleBinding
    private lateinit var articleRepository: ArticleRepository
    private val contentViews = mutableListOf<View>()
    private var selectedImageUri: Uri? = null
    private lateinit var article: Article

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            binding.imageViewPreview.setImageURI(uri)
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
        binding.buttonAddParagraph.setOnClickListener { addParagraphField() }
        binding.buttonAddHeading.setOnClickListener { addHeadingField() }
        binding.buttonUploadImage.setOnClickListener { imagePickerLauncher.launch("image/*") }
        binding.buttonUpdateArticle.setOnClickListener { updateArticle() }
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

                // Load content items
                article.content.forEach { contentItem ->
                    when (contentItem.type) {
                        "paragraph" -> addParagraphField(contentItem.text)
                        "heading" -> addHeadingField(contentItem.text)
                    }
                }
            }.onFailure { error ->
                Toast.makeText(this@EditArticleActivity, "Error loading article: ${error.message}", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun addParagraphField(text: String = "") {
        val editText = createEditText("Enter paragraph", InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE)
        editText.textSize = 16f // Normal text size for paragraphs
        editText.setText(text)
        binding.linearLayoutContentContainer.addView(editText)
        contentViews.add(editText)
    }

    private fun addHeadingField(text: String = "") {
        val editText = createEditText("Enter heading", InputType.TYPE_CLASS_TEXT)
        editText.textSize = 24f // Larger text size for headings
        editText.setText(text)
        binding.linearLayoutContentContainer.addView(editText)
        contentViews.add(editText)
    }

    private fun createEditText(hint: String, inputType: Int): EditText {
        return EditText(this).apply {
            this.hint = hint
            this.inputType = inputType
            this.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 16, 0, 16)
            }
        }
    }

    private fun updateArticle() {
        val title = binding.editTextTitle.text.toString()
        val description = binding.editTextDescription.text.toString()
        val contentList = mutableListOf<ContentItem>()

        for (view in contentViews) {
            if (view is EditText) {
                val text = view.text.toString()
                if (text.isNotBlank()) {
                    val contentType = if (view.textSize > 18f) "heading" else "paragraph"
                    contentList.add(ContentItem(type = contentType, text = text))
                }
            }
        }

        if (title.isBlank() || description.isBlank() || contentList.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val updatedArticle = article.copy(
                    title = title,
                    description = description,
                    content = contentList
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

