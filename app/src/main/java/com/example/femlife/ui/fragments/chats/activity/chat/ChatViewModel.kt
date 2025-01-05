package com.example.femlife.ui.fragments.chats.activity.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.femlife.data.chat.Message
import com.example.femlife.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    private val chatRepository = ChatRepository()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    fun loadMessages(chatId: String) {
        viewModelScope.launch {
            chatRepository.getChatMessages(chatId)
                .collect { messages ->
                    _messages.value = messages
                }
        }
    }

    fun sendMessage(chatId: String, message: String) {
        viewModelScope.launch {
            try {
                chatRepository.sendMessage(chatId, message)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}