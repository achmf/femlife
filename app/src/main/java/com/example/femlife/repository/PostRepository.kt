package com.example.femlife.repository

import android.content.Context
import android.net.Uri
import com.example.femlife.config.ApiConfig
import com.example.femlife.data.femtalk.Post
import com.google.firebase.firestore.FirebaseFirestore
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID

class PostRepository(private val context: Context, private val apiConfig: ApiConfig) {  // Accept Context and ApiConfig

    private val firestore: FirebaseFirestore = apiConfig.firestore  // Use firestore from ApiConfig
    private val supabase = apiConfig.supabase  // Use supabase from ApiConfig

    suspend fun getPosts(): Result<List<Post>> = withContext(Dispatchers.IO) {
        try {
            val posts = firestore.collection("posts")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()
                .toObjects(Post::class.java)
            Result.success(posts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun likePost(postId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            firestore.collection("posts").document(postId)
                .update("likes", com.google.firebase.firestore.FieldValue.increment(1))
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addComment(postId: String, comment: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val commentData = hashMapOf(
                "text" to comment,
                "timestamp" to com.google.firebase.Timestamp.now()
            )
            firestore.collection("posts").document(postId)
                .collection("comments")
                .add(commentData)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createPost(imageUri: Uri, caption: String): Result<Post> = withContext(Dispatchers.IO) {
        try {
            val imageUrl = uploadImageToSupabase(imageUri)
            val post = Post(
                imageUrl = imageUrl,
                caption = caption,
                likes = 0,
                timestamp = com.google.firebase.Timestamp.now(),
            )
            val documentReference = firestore.collection("posts").add(post).await()
            Result.success(post.copy(id = documentReference.id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun uploadImageToSupabase(uri: Uri): String = withContext(Dispatchers.IO) {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalStateException("Cannot open input stream for URI")

        val fileName = "post_images/${UUID.randomUUID()}.jpg"
        supabase.storage["posts"].upload(fileName, inputStream.readBytes())  // Correct usage of readBytes()

        return@withContext supabase.storage["posts"].publicUrl(fileName) // Return the URL
    }
}
