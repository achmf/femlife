package com.example.femlife.data.workshop

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WorkshopItem(
    var id: String? = null,
    var imageUrl: String,
    var title: String,
    var description: String,
    var link: String
) : Parcelable
