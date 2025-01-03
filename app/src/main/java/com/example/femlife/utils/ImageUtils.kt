package com.example.femlife.utils

import android.widget.ImageView
import com.example.femlife.R

object ImageUtils {
    private val avatars = listOf(
        R.drawable.ava1,
        R.drawable.ava2,
        R.drawable.ava3,
        R.drawable.ava4,
        R.drawable.ava5,
        R.drawable.ava6
    )

    fun loadAvatar(imageView: ImageView, avatarResId: Int?) {
        val resourceId = avatarResId ?: R.drawable.default_avatar
        imageView.setImageResource(resourceId)
    }
}