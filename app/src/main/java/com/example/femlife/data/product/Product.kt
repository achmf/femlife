package com.example.femlife.data.product

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    @DocumentId val id: String = "",
    val name: String = "",
    val image: String = "",
    val price: Double = 0.0,
    val adminWhatsApp: String = "" // New field for admin's WhatsApp number
) : Parcelable