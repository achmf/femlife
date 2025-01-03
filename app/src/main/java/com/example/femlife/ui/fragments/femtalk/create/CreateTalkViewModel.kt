package com.example.femlife.ui.fragments.femtalk.create

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.femlife.config.ApiConfig
import com.example.femlife.data.femtalk.Post
import com.example.femlife.repository.PostRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class CreateTalkViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PostRepository(application.applicationContext, ApiConfig)
    private val auth = FirebaseAuth.getInstance()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _postCreated = MutableLiveData<Post?>()
    val postCreated: LiveData<Post?> = _postCreated

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun createPost(imageUri: Uri, caption: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userId = auth.currentUser?.uid
                    ?: throw IllegalStateException("User must be logged in to create a post")

                val result = repository.createPost(imageUri, caption, userId)
                result.onSuccess { post ->
                    _postCreated.value = post
                }.onFailure { e ->
                    _error.value = "Failed to create post: ${e.message}"
                    _postCreated.value = null
                }
            } catch (e: Exception) {
                _error.value = "An error occurred: ${e.message}"
                _postCreated.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }
}

