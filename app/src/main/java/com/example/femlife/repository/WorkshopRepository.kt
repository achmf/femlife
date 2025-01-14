package com.example.femlife.repository

import android.content.Context
import android.net.Uri
import com.example.femlife.data.workshop.WorkshopItem
import com.example.femlife.config.ApiConfig
import com.google.firebase.firestore.FirebaseFirestore
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID

class WorkshopRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val workshopCollection = firestore.collection("workshops")
    private val supabase = ApiConfig.supabase // Assume you have Supabase configured

    // Get a list of all workshops
    suspend fun getWorkshops(): List<WorkshopItem> {
        return try {
            val snapshot = workshopCollection.get().await()
            snapshot.documents.map { document ->
                WorkshopItem(
                    id = document.id, // Set the ID from Firestore document
                    title = document.getString("title") ?: "",
                    description = document.getString("description") ?: "",
                    imageUrl = document.getString("imageUrl") ?: "",
                    link = document.getString("link") ?: ""
                )
            }
        } catch (e: Exception) {
            emptyList() // Return empty list if an error occurs
        }
    }

    // Add a new workshop to Firestore
    suspend fun addWorkshop(workshopItem: WorkshopItem): String {
        return try {
            val documentReference = workshopCollection.add(workshopItem).await()
            documentReference.id // Return the ID of the newly created workshop
        } catch (e: Exception) {
            throw e // Re-throw exception if needed
        }
    }

    // Upload image to Supabase and return the image URL
    suspend fun uploadImage(context: Context, imageUri: Uri): String {
        return withContext(Dispatchers.IO) {
            val inputStream = context.contentResolver.openInputStream(imageUri)
                ?: throw IllegalStateException("Cannot open input stream for URI")

            val fileName = "workshop_images/${UUID.randomUUID()}.jpg"
            supabase.storage["workshop-images"].upload(fileName, inputStream.readBytes())
            supabase.storage["workshop-images"].publicUrl(fileName)
        }
    }

    // Fungsi untuk menghapus workshop dari Firestore
    suspend fun deleteWorkshop(workshopItem: WorkshopItem) {
        try {
            workshopItem.id?.let { workshopId ->
                workshopCollection.document(workshopId).delete().await()
            }
        } catch (e: Exception) {
            // Log error jika diperlukan
            throw e // Rethrow error if needed
        }
    }

    // Fungsi untuk memperbarui workshop yang sudah ada di Firestore
    suspend fun updateWorkshop(workshopItem: WorkshopItem) {
        try {
            workshopItem.id?.let { workshopId ->
                workshopCollection.document(workshopId).set(workshopItem).await()
            }
        } catch (e: Exception) {
            // Log error jika diperlukan
            throw e // Rethrow error if needed
        }
    }
}
