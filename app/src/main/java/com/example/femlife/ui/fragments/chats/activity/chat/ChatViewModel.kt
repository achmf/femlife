package com.example.femlife.ui.fragments.chats.activity.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.femlife.data.chat.ChatGroup
import com.example.femlife.data.chat.Message
import com.example.femlife.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    private val chatRepository = ChatRepository()

    private val _chatGroups = MutableStateFlow<List<ChatGroup>>(emptyList())
    val chatGroups: StateFlow<List<ChatGroup>> = _chatGroups

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    init {
        loadChatGroups()
    }

    private fun loadChatGroups() {
        viewModelScope.launch {
            chatRepository.getChatGroups().collect { groups ->
                val updatedGroups = groups.map { group ->
                    chatRepository.getLastMessage(group.id).combine(kotlinx.coroutines.flow.flowOf(group)) { lastMessage, chatGroup ->
                        chatGroup.copy(
                            lastMessage = lastMessage?.message ?: "",
                            lastMessageTimestamp = lastMessage?.timestamp ?: 0
                        )
                    }
                }
                combine(updatedGroups) { it.toList() }.collect { _chatGroups.value = it }
            }
        }
    }

    fun loadMessages(chatId: String) {
        viewModelScope.launch {
            chatRepository.getChatMessages(chatId).collect { messages ->
                _messages.value = messages
            }
        }
    }

    fun sendMessage(chatId: String, message: String) {
        viewModelScope.launch {
            try {
                chatRepository.sendMessage(chatId, message)
            } catch (e: Exception) {
                // Handle error (e.g., show a toast or update UI)
            }
        }
    }
}

