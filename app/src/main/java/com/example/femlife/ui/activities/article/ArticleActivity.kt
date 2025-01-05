// ArticleActivity.kt
package com.example.femlife.ui.activities.article

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.femlife.R
import com.example.femlife.databinding.ActivityArticleBinding
import com.example.femlife.data.article.Article
import com.example.femlife.ui.activities.article.manager.create.CreateArticleActivity
import com.example.femlife.ui.activities.article.detail.DetailArticleActivity
import com.example.femlife.ui.activities.article.viewmodel.ArticleViewModel
import com.example.femlife.ui.activities.article.manager.ArticleManagerActivity

class ArticleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityArticleBinding
    private lateinit var adapter: ArticleAdapter

    private val articleViewModel: ArticleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        observeArticles()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Edukasi kesehatan"

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_article, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_manage_articles -> {
                val intent = Intent(this, ArticleManagerActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupRecyclerView() {
        adapter = ArticleAdapter(mutableListOf()) { article ->
            navigateToDetailArticle(article)
        }
        binding.recyclerViewArticles.layoutManager = GridLayoutManager(this, 2)
        binding.recyclerViewArticles.adapter = adapter
    }

    private fun observeArticles() {
        articleViewModel.articles.observe(this) { articles ->
            adapter.updateArticles(articles)
        }
    }

    private fun navigateToDetailArticle(article: Article) {
        val intent = Intent(this, DetailArticleActivity::class.java).apply {
            putExtra(DetailArticleActivity.EXTRA_ARTICLE_ID, article.id)
        }
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        // Refresh the articles when the activity resumes
        articleViewModel.fetchArticles()
    }
}
