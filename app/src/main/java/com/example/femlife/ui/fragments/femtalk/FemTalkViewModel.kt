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
import com.example.femlife.data.femtalk.Comment

class FemTalkViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PostRepository(application.applicationContext, ApiConfig)  // Pass application context

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> = _posts

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _postDetails = MutableLiveData<Post>()
    val postDetails: LiveData<Post> = _postDetails

    private val _comments = MutableLiveData<List<Comment>>()
    val comments: LiveData<List<Comment>> = _comments

    private val _commentAdded = MutableLiveData<Boolean>()
    val commentAdded: LiveData<Boolean> = _commentAdded


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

    fun getPostDetails(postId: String) {
        viewModelScope.launch {
            try {
                val post = repository.getPostDetails(postId)
                _postDetails.value = post
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun getComments(postId: String) {
        viewModelScope.launch {
            try {
                val comments = repository.getComments(postId)
                _comments.value = comments
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun addComment(postId: String, comment: String) {
        viewModelScope.launch {
            try {
                repository.addComment(postId, comment)
                _commentAdded.value = true
            } catch (e: Exception) {
                _commentAdded.value = false
            }
        }
    }
}

