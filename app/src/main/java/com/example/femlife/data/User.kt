package com.example.femlife.data

import android.os.Parcelable
import com.example.femlife.R
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val name: String = "",
    val dateOfBirth: String = "",
    val location: String = "",
    val phoneNumber: String = "",
    val age: String = "",
    val email: String = "",
    val profileCompleted: Boolean = false,
    val avatar: Int = R.drawable.default_avatar,
    val role: String = "user"
) : Parcelable
