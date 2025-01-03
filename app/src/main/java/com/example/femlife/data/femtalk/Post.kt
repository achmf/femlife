package com.example.femlife.data.femtalk

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Post(
    @DocumentId val id: String = "",
    val userId: String = "",
    val imageUrl: String = "",
    val caption: String = "",
    val likes: Int = 0,
    val likedBy: List<String> = emptyList(),
    val timestamp: Timestamp = Timestamp.now(),
    val comments: List<Comment> = emptyList()
)

