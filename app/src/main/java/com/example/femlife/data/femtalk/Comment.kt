package com.example.femlife.data.femtalk

import com.google.firebase.Timestamp

data class Comment(
    val id: String = "",
    val username: String = "",
    val text: String = "",
    val timestamp: Timestamp = Timestamp.now()
)