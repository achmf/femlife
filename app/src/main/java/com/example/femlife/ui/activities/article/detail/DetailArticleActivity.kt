package com.example.femlife.ui.activities.article.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.femlife.data.article.Article
import com.example.femlife.databinding.ActivityDetailArticleBinding
import com.example.femlife.repository.ArticleRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class DetailArticleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailArticleBinding
    private lateinit var articleRepository: ArticleRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        articleRepository = ArticleRepository(this)

        val articleId = intent.getStringExtra(EXTRA_ARTICLE_ID)
        if (articleId != null) {
            setupToolbar()
            fetchAndDisplayArticle(articleId)
        } else {
            finish()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun fetchAndDisplayArticle(articleId: String) {
        lifecycleScope.launch {
            val result = articleRepository.getArticleById(articleId)
            result.onSuccess { article ->
                displayArticleDetails(article)
            }.onFailure { error ->
                // Handle error (e.g., show error message and finish activity)
            }
        }
    }

    private fun displayArticleDetails(article: Article) {
        supportActionBar?.title = article.title
        binding.textViewTitle.text = article.title
        binding.textViewDescription.text = article.description

        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id"))
        binding.textViewCreatedAt.text = dateFormat.format(article.createdAt)

        Glide.with(this)
            .load(article.imageUrl)
            .centerCrop()
            .into(binding.imageViewArticle)
    }

    companion object {
        const val EXTRA_ARTICLE_ID = "extra_article_id"
    }
}