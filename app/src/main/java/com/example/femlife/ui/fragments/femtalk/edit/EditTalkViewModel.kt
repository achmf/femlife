package com.example.femlife.ui.fragments.femtalk.edit

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.femlife.config.ApiConfig
import com.example.femlife.data.femtalk.Post
import com.example.femlife.repository.PostRepository
import kotlinx.coroutines.launch

class EditTalkViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PostRepository(application.applicationContext, ApiConfig)

    private val _post = MutableLiveData<Post>()
    val post: LiveData<Post> = _post

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _postUpdated = MutableLiveData<Boolean>()
    val postUpdated: LiveData<Boolean> = _postUpdated

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun loadPostDetails(postId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val postDetails = repository.getPostDetails(postId)
                _post.value = postDetails
            } catch (e: Exception) {
                _error.value = "Failed to load post details: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updatePost(newCaption: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val currentPost = _post.value ?: throw IllegalStateException("Post not loaded")
                val updatedPost = currentPost.copy(caption = newCaption)
                repository.updatePost(updatedPost)
                _postUpdated.value = true
            } catch (e: Exception) {
                _error.value = "Failed to update post: ${e.message}"
                _postUpdated.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }
}

