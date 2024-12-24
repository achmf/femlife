package com.example.femlife.ui.fragments.femtalk

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.femlife.config.ApiConfig
import com.example.femlife.data.femtalk.Post
import com.example.femlife.repository.PostRepository
import kotlinx.coroutines.launch

class FemTalkViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PostRepository(application.applicationContext, ApiConfig)  // Pass application context

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> = _posts

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        refreshPosts()
    }

    fun refreshPosts() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.getPosts()
                _posts.value = result.getOrNull() ?: emptyList()
            } catch (e: Exception) {
                _posts.value = emptyList() // Handle errors gracefully
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun likePost(post: Post) {
        viewModelScope.launch {
            repository.likePost(post.id)
            refreshPosts()
        }
    }

    fun addComment(postId: String, comment: String) {
        viewModelScope.launch {
            repository.addComment(postId, comment)
            refreshPosts()
        }
    }
}
