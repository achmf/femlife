package com.example.femlife.ui.activities.article.manager

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.femlife.R
import com.example.femlife.data.article.Article
import com.example.femlife.databinding.ActivityArticleManagerBinding
import com.example.femlife.repository.ArticleRepository
import com.example.femlife.ui.activities.article.manager.create.CreateArticleActivity
import com.example.femlife.ui.activities.article.manager.edit.EditArticleActivity
import kotlinx.coroutines.launch

class ArticleManagerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityArticleManagerBinding
    private lateinit var articleAdapter: ArticleManagerAdapter
    private lateinit var articleRepository: ArticleRepository // Declare ArticleRepository here

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArticleManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Instantiate ArticleRepository manually
        articleRepository = ArticleRepository(this)

        setupToolbar()
        setupRecyclerView()
        loadArticles()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Kelola Artikel"

        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        articleAdapter = ArticleManagerAdapter(
            onEdit = { article -> navigateToEditArticle(article) },
            onDelete = { article -> deleteArticle(article) }
        )
        binding.recyclerViewArticles.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewArticles.adapter = articleAdapter
    }

    private fun loadArticles() {
        lifecycleScope.launch {
            try {
                val result = articleRepository.getArticles()
                result.onSuccess { articles ->
                    articleAdapter.submitList(articles)
                }.onFailure { error ->
                    Toast.makeText(this@ArticleManagerActivity, "Failed to load articles: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ArticleManagerActivity, "An error occurred: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToEditArticle(article: Article) {
        val intent = Intent(this, EditArticleActivity::class.java)
        intent.putExtra("articleId", article.id)
        startActivity(intent)
    }

    private fun deleteArticle(article: Article) {
        lifecycleScope.launch {
            try {
                val result = articleRepository.deleteArticle(article.id)
                result.onSuccess {
                    loadArticles() // Reload the list after deletion
                    Toast.makeText(this@ArticleManagerActivity, "Article deleted successfully", Toast.LENGTH_SHORT).show()
                }.onFailure { error ->
                    Toast.makeText(this@ArticleManagerActivity, "Failed to delete article: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ArticleManagerActivity, "An error occurred: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_article_manager_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add_article -> {
                navigateToAddArticle()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun navigateToAddArticle() {
        val intent = Intent(this, CreateArticleActivity::class.java)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        loadArticles() // Memastikan list artikel diperbarui setiap kali aktivitas kembali ke layar
    }
}
