package com.example.femlife.repository

import android.content.Context
import android.net.Uri
import com.example.femlife.config.ApiConfig
import com.example.femlife.data.femtalk.Post
import com.example.femlife.data.femtalk.Comment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID

class PostRepository(private val context: Context, private val apiConfig: ApiConfig) {
    private val firestore: FirebaseFirestore = apiConfig.firestore
    private val supabase = apiConfig.supabase
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

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

    suspend fun addComment(postId: String, commentText: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val userId = auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")
            val commentData = Comment(
                userId = userId,
                text = commentText,
                timestamp = com.google.firebase.Timestamp.now()
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

    suspend fun createPost(imageUri: Uri, caption: String, userId: String): Result<Post> = withContext(Dispatchers.IO) {
        try {
            val imageUrl = uploadImageToSupabase(imageUri)
            val post = Post(
                userId = userId,
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

    suspend fun getPostDetails(postId: String): Post = withContext(Dispatchers.IO) {
        firestore.collection("posts").document(postId).get().await().toObject(Post::class.java)
            ?: throw Exception("Post not found")
    }

    suspend fun getComments(postId: String): List<Comment> = withContext(Dispatchers.IO) {
        firestore.collection("posts").document(postId).collection("comments")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .await()
            .toObjects(Comment::class.java)
    }

    suspend fun toggleLike(postId: String): Result<Post> = withContext(Dispatchers.IO) {
        try {
            val userId = auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")
            val postRef = firestore.collection("posts").document(postId)

            val updatedPost = firestore.runTransaction { transaction ->
                val post = transaction.get(postRef).toObject(Post::class.java)
                    ?: throw IllegalStateException("Post not found")

                val updatedLikedBy = if (userId in post.likedBy) {
                    post.likedBy - userId
                } else {
                    post.likedBy + userId
                }

                val updatedLikes = updatedLikedBy.size

                val updatedPost = post.copy(
                    likes = updatedLikes,
                    likedBy = updatedLikedBy
                )

                transaction.set(postRef, updatedPost)
                updatedPost
            }.await()

            Result.success(updatedPost)
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

    suspend fun deletePost(postId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            firestore.collection("posts").document(postId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updatePost(post: Post): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            firestore.collection("posts").document(post.id).set(post).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun editComment(postId: String, commentId: String, newText: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            firestore.collection("posts").document(postId)
                .collection("comments").document(commentId)
                .update("text", newText)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteComment(postId: String, commentId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            firestore.collection("posts").document(postId)
                .collection("comments").document(commentId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

