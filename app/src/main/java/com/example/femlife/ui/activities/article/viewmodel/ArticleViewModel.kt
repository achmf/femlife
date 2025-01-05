package com.example.femlife.ui.activities.article.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.femlife.data.article.Article
import com.example.femlife.repository.ArticleRepository
import kotlinx.coroutines.launch

class ArticleViewModel(application: Application) : AndroidViewModel(application) {

    private val articleRepository = ArticleRepository(application)
    private val _articles = MutableLiveData<List<Article>>()
    val articles: LiveData<List<Article>> get() = _articles

    init {
        fetchArticles()
    }

    fun fetchArticles() {
        viewModelScope.launch {
            val result = articleRepository.getArticles()
            result.onSuccess { articleList ->
                _articles.postValue(articleList)
            }.onFailure { error ->
                // Handle error (e.g., show error message)
            }
        }
    }
}

