package com.example.femlife.repository

import com.example.femlife.data.chat.ChatGroup
import com.example.femlife.data.chat.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map

class ChatRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun getChatGroups(): Flow<List<ChatGroup>> = callbackFlow {
        val subscription = db.collection("chatGroups")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val chatGroups = snapshot?.documents?.mapNotNull { doc ->
                    val id = doc.id
                    val name = doc.getString("name") ?: return@mapNotNull null
                    val imageUrl = doc.getString("imageUrl") ?: ""

                    ChatGroup(
                        id = id,
                        name = name,
                        imageUrl = imageUrl,
                        lastMessage = "",
                        lastMessageTimestamp = 0
                    )
                } ?: emptyList()

                trySend(chatGroups)
            }

        awaitClose { subscription.remove() }
    }.map { chatGroups ->
        if (chatGroups.none { it.name == "Femnonym" }) {
            val defaultGroup = ChatGroup(
                id = "femnonym",
                name = "Femnonym",
                imageUrl = "",
                lastMessage = "",
                lastMessageTimestamp = 0
            )
            listOf(defaultGroup) + chatGroups
        } else {
            chatGroups
        }
    }

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

    fun getLastMessage(chatId: String): Flow<Message?> = callbackFlow {
        val subscription = db.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(1)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val lastMessage = snapshot?.documents?.firstOrNull()?.toObject(Message::class.java)
                trySend(lastMessage)
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
            senderName = currentUser.displayName ?: "Anonymous",
            senderAvatar = currentUser.photoUrl?.toString() ?: ""
        )

        db.collection("chats")
            .document(chatId)
            .collection("messages")
            .document(messageId)
            .set(newMessage)
    }
}

