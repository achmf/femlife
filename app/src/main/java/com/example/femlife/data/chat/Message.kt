package com.example.femlife.data.chat

data class Message(
    val id: String = "",
    val senderId: String = "",
    val message: String = "",
    val timestamp: Long = 0,
    val senderName: String = "",
    val senderAvatar: String = ""
)