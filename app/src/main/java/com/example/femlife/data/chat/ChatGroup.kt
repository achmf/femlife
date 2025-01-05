package com.example.femlife.data.chat

data class ChatGroup(
    val id: String,
    val name: String,
    val imageUrl: String,
    val lastMessage: String,
    val lastMessageTimestamp: Long,
)

