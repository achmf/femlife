package com.example.femlife.repository

import android.content.Context
import android.net.Uri
import com.example.femlife.data.product.Product
import com.google.firebase.firestore.FirebaseFirestore
import com.example.femlife.config.ApiConfig
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class ProductRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val productsCollection = firestore.collection("products")
    private val supabase = ApiConfig.supabase

    suspend fun getAllProducts(): List<Product> {
        return productsCollection.get().await().documents.mapNotNull { document ->
            document.toObject(Product::class.java)?.copy(id = document.id)
        }
    }

    suspend fun addProduct(product: Product): String {
        val documentReference = productsCollection.add(product).await()
        return documentReference.id
    }

    suspend fun updateProduct(product: Product) {
        productsCollection.document(product.id).set(product).await()
    }

    suspend fun deleteProduct(productId: String) {
        productsCollection.document(productId).delete().await()
    }

    suspend fun getProduct(productId: String): Product? {
        val document = productsCollection.document(productId).get().await()
        return document.toObject(Product::class.java)?.copy(id = document.id)
    }

    suspend fun uploadImage(context: Context, imageUri: Uri): String {
        return withContext(Dispatchers.IO) {
            val inputStream = context.contentResolver.openInputStream(imageUri)
                ?: throw IllegalStateException("Cannot open input stream for URI")

            val fileName = "product_images/${UUID.randomUUID()}.jpg"
            supabase.storage["product-images"].upload(fileName, inputStream.readBytes())
            supabase.storage["product-images"].publicUrl(fileName)
        }
    }
}