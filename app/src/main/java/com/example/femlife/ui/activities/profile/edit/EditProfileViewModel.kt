package com.example.femlife.ui.activities.profile.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.femlife.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditProfileViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()

    private val _userData = MutableLiveData<User>()
    val userData: LiveData<User> = _userData

    private val _operationStatus = MutableLiveData<String>()
    val operationStatus: LiveData<String> = _operationStatus

    init {
        fetchUserData()
    }

    private fun fetchUserData() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            _operationStatus.value = "User not found. Please login again."
            return
        }

        firestore.collection("users").document(currentUser.uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val user = document.toObject(User::class.java)
                    _userData.value = user
                } else {
                    _operationStatus.value = "User data not found."
                }
            }
            .addOnFailureListener { e ->
                _operationStatus.value = "Failed to load user data: ${e.message}"
            }
    }

    fun updateUserData(updatedData: Map<String, Any>) {
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            _operationStatus.value = "User not found. Please login again."
            return
        }

        firestore.collection("users").document(currentUser.uid)
            .update(updatedData)
            .addOnSuccessListener {
                _operationStatus.value = "Data updated successfully"
            }
            .addOnFailureListener { e ->
                _operationStatus.value = "Failed to update data: ${e.message}"
            }
    }
}
