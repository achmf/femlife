package com.example.femlife.data.femtalk

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Comment(
    @DocumentId val id: String = "",
    val userId: String = "",
    val text: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val likes: Int = 0
)