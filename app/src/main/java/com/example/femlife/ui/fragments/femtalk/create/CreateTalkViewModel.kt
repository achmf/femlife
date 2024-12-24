package com.example.femlife.ui.fragments.femtalk.create

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.femlife.config.ApiConfig
import com.example.femlife.repository.PostRepository
import kotlinx.coroutines.launch

class CreateTalkViewModel(application: Application) : AndroidViewModel(application) {

    // Pass both application context and ApiConfig to PostRepository
    private val repository = PostRepository(application.applicationContext, ApiConfig)

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _postCreated = MutableLiveData<Boolean>()
    val postCreated: LiveData<Boolean> = _postCreated

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun createPost(imageUri: Uri, caption: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.createPost(imageUri, caption)
                result.onSuccess {
                    _postCreated.value = true
                }.onFailure { e ->
                    _error.value = "Failed to create post: ${e.message}"
                    _postCreated.value = false
                }
            } catch (e: Exception) {
                _error.value = "An error occurred: ${e.message}"
                _postCreated.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }
}