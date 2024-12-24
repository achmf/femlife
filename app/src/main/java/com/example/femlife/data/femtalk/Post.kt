package com.example.femlife.data.femtalk

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Post(
    @DocumentId val id: String = "",
    val username: String = "",
    val imageUrl: String = "",
    val caption: String = "",
    val likes: Int = 0,
    val timestamp: Timestamp = Timestamp.now(),
    val comments: List<Comment> = emptyList()
)