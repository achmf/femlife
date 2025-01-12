package com.example.femlife.repository

import android.content.Context
import android.net.Uri
import com.example.femlife.config.ApiConfig
import com.example.femlife.data.article.Article
import com.example.femlife.data.article.ContentItem
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.UUID

class ArticleRepository(private val context: Context) {
    private val firestore = ApiConfig.firestore
    private val supabase = ApiConfig.supabase

    suspend fun createArticle(
        title: String,
        description: String,
        content: List<ContentItem>,
        imageUri: Uri?
    ): Result<Article> = withContext(Dispatchers.IO) {
        try {
            val imageUrl = imageUri?.let { uploadImageToSupabase(it) } ?: ""
            val article = Article(
                title = title,
                description = description,
                content = content,
                imageUrl = imageUrl,
                createdAt = Date()
            )
            firestore.collection("articles").add(article).await()

            // Tambahkan notifikasi untuk semua user
            createNotificationForNewArticle(article)

            Result.success(article)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Fungsi baru untuk membuat notifikasi
    private suspend fun createNotificationForNewArticle(article: Article) = withContext(Dispatchers.IO) {
        try {
            val notificationData = mapOf(
                "title" to "Artikel Baru: ${article.title}",
                "message" to "Baca artikel baru kami: ${article.description}",
                "type" to "NEW_ARTICLE",
                "timestamp" to Date(),
                "userId" to "ALL_USERS" // Menandai notifikasi untuk semua user
            )
            firestore.collection("notifications").add(notificationData).await()
        } catch (e: Exception) {
            // Log error jika ada masalah saat membuat notifikasi
            e.printStackTrace()
        }
    }

    private suspend fun uploadImageToSupabase(uri: Uri): String = withContext(Dispatchers.IO) {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalStateException("Cannot open input stream for URI")

        val fileName = "article_images/${UUID.randomUUID()}.jpg"
        supabase.storage["article-images"].upload(fileName, inputStream.readBytes())

        supabase.storage["article-images"].publicUrl(fileName)
    }

    suspend fun getArticles(): Result<List<Article>> = withContext(Dispatchers.IO) {
        try {
            val snapshot = firestore.collection("articles")
                .orderBy("createdAt")
                .get()
                .await()
            val articles = snapshot.documents.map { doc ->
                doc.toObject(Article::class.java)?.copy(id = doc.id) ?: Article(id = doc.id)
            }
            Result.success(articles)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getArticleById(id: String): Result<Article> = withContext(Dispatchers.IO) {
        try {
            val document = firestore.collection("articles").document(id).get().await()
            val article = document.toObject(Article::class.java)?.copy(id = document.id)
            if (article != null) {
                Result.success(article)
            } else {
                Result.failure(NoSuchElementException("Article not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteArticle(articleId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            firestore.collection("articles").document(articleId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateArticle(article: Article): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            firestore.collection("articles").document(article.id).set(article).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateArticleWithImage(article: Article, imageUri: Uri?): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val imageUrl = if (imageUri != null) uploadImageToSupabase(imageUri) else article.imageUrl
            val updatedArticle = article.copy(imageUrl = imageUrl)
            firestore.collection("articles").document(article.id).set(updatedArticle).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
