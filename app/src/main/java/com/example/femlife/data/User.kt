package com.example.femlife.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val name: String = "",
    val dateOfBirth: String = "",
    val location: String = "",
    val phoneNumber: String = "",
    val age: String = "",
    val email: String = "",
    val isProfileCompleted: Boolean = false
) : Parcelable
