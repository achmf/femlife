package com.example.femlife.repository

import com.example.femlife.data.chat.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class ChatRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun getChatMessages(chatId: String): Flow<List<Message>> = callbackFlow {
        val subscription = db.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val messages = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Message::class.java)
                } ?: emptyList()

                trySend(messages)
            }

        awaitClose { subscription.remove() }
    }

    suspend fun sendMessage(chatId: String, message: String) {
        val messageId = db.collection("chats")
            .document(chatId)
            .collection("messages")
            .document().id

        val currentUser = auth.currentUser ?: throw IllegalStateException("User not logged in")

        val newMessage = Message(
            id = messageId,
            senderId = currentUser.uid,
            message = message,
            timestamp = System.currentTimeMillis(),
            senderName = "Anonymous", // Or get from user profile
            senderAvatar = "" // Optional avatar URL
        )

        db.collection("chats")
            .document(chatId)
            .collection("messages")
            .document(messageId)
            .set(newMessage)
    }
}