package com.example.femlife.utils

import android.widget.ImageView
import com.example.femlife.R

object ImageUtils {

    fun loadAvatar(imageView: ImageView, avatarResId: Int?) {
        val resourceId = avatarResId ?: R.drawable.default_avatar
        imageView.setImageResource(resourceId)
    }
}

