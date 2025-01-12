package com.example.femlife.data.article

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ContentItem(
    val type: String = "",
    val text: String = ""
) : Parcelable
