package com.example.femlife.ui.activities.article.manager.create

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
import com.example.femlife.data.article.ContentItem
import com.example.femlife.databinding.ActivityCreateArticleBinding
import com.example.femlife.repository.ArticleRepository
import kotlinx.coroutines.launch

class CreateArticleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateArticleBinding
    private lateinit var articleRepository: ArticleRepository
    private val contentViews = mutableListOf<View>()
    private var selectedImageUri: Uri? = null

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            binding.imageViewPreview.setImageURI(uri)
            binding.imageViewPreview.visibility = View.VISIBLE
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
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Create Article"

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setupListeners() {
        binding.buttonAddParagraph.setOnClickListener { addParagraphField() }
        binding.buttonAddHeading.setOnClickListener { addHeadingField() }
        binding.buttonUploadImage.setOnClickListener { imagePickerLauncher.launch("image/*") }
        binding.buttonCreateArticle.setOnClickListener { createArticle() }
    }

    private fun addParagraphField() {
        val editText = createEditText("Enter paragraph", InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE)
        editText.textSize = 16f // Normal text size for paragraphs
        binding.linearLayoutContentContainer.addView(editText)
        contentViews.add(editText)
    }

    private fun addHeadingField() {
        val editText = createEditText("Enter heading", InputType.TYPE_CLASS_TEXT)
        editText.textSize = 24f // Larger text size for headings
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

    private fun createArticle() {
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
                val result = articleRepository.createArticle(title, description, contentList, selectedImageUri)
                result.onSuccess {
                    Toast.makeText(this@CreateArticleActivity, "Article created successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }.onFailure {
                    Toast.makeText(this@CreateArticleActivity, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@CreateArticleActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
