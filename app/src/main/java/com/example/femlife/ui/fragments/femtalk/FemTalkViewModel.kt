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
import com.google.firebase.auth.FirebaseAuth

class FemTalkViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PostRepository(application.applicationContext, ApiConfig)
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

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

    private val _individualPost = MutableLiveData<Post>()
    val individualPost: LiveData<Post> = _individualPost

    private val _likeToggled = MutableLiveData<Boolean>()
    val likeToggled: LiveData<Boolean> = _likeToggled

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

    fun toggleLike(postId: String) {
        viewModelScope.launch {
            try {
                val result = repository.toggleLike(postId)
                if (result.isSuccess) {
                    _likeToggled.value = true
                    updateIndividualPost(postId)
                } else {
                    _likeToggled.value = false
                }
            } catch (e: Exception) {
                _likeToggled.value = false
            }
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

    private fun refreshPostDetails(postId: String) {
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

    fun deletePost(post: Post) {
        viewModelScope.launch {
            try {
                repository.deletePost(post.id)
                refreshPosts()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }


    fun editComment(postId: String, commentId: String, newText: String) {
        viewModelScope.launch {
            try {
                repository.editComment(postId, commentId, newText)
                getComments(postId)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun deleteComment(postId: String, commentId: String) {
        viewModelScope.launch {
            try {
                repository.deleteComment(postId, commentId)
                getComments(postId)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    fun updateIndividualPost(postId: String) {
        viewModelScope.launch {
            try {
                val updatedPost = repository.getPostDetails(postId)
                _individualPost.value = updatedPost

                // Update the post in the list of all posts
                _posts.value = _posts.value?.map {
                    if (it.id == postId) updatedPost else it
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}

