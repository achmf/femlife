package com.example.femlife.ui.activities.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.femlife.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()

    // Daftar avatar yang tersedia
    val avatars = listOf(
        R.drawable.ava1,
        R.drawable.ava2,
        R.drawable.ava3,
        R.drawable.ava4,
        R.drawable.ava5,
        R.drawable.ava6
    )

    // LiveData untuk data pengguna
    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> get() = _userName

    private val _userAge = MutableLiveData<String>()
    val userAge: LiveData<String> get() = _userAge

    private val _userBirthplace = MutableLiveData<String>()
    val userBirthplace: LiveData<String> get() = _userBirthplace

    private val _userLocation = MutableLiveData<String>()
    val userLocation: LiveData<String> get() = _userLocation

    private val _userPhone = MutableLiveData<String>()
    val userPhone: LiveData<String> get() = _userPhone

    private val _userEmail = MutableLiveData<String>()
    val userEmail: LiveData<String> get() = _userEmail

    private val _userAvatar = MutableLiveData<Int>()
    val userAvatar: LiveData<Int> get() = _userAvatar

    // LiveData untuk status operasi
    private val _operationStatus = MutableLiveData<String>()
    val operationStatus: LiveData<String> get() = _operationStatus

    // Method untuk memuat data pengguna dari Firestore
    fun fetchUserData() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            _operationStatus.postValue("User tidak ditemukan. Silakan login kembali.")
            return
        }

        val userId = currentUser.uid
        firestore.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    _userName.postValue(document.getString("name") ?: "Tidak diketahui")
                    _userAge.postValue("${document.getString("age")} Tahun")
                    _userBirthplace.postValue(document.getString("dateOfBirth") ?: "Tidak diketahui")
                    _userLocation.postValue(document.getString("location") ?: "Tidak diketahui")
                    _userPhone.postValue(document.getString("phoneNumber") ?: "Tidak diketahui")
                    _userEmail.postValue(document.getString("email") ?: "Tidak diketahui")

                    val avatarResId = document.getLong("avatar")?.toInt()
                    if (avatarResId != null) {
                        _userAvatar.postValue(avatarResId)
                    } else {
                        _userAvatar.postValue(R.drawable.default_avatar) // Gunakan default hanya jika avatar tidak ada
                    }

                    _operationStatus.postValue("Data pengguna berhasil dimuat.")
                } else {
                    _operationStatus.postValue("Data pengguna tidak ditemukan.")
                }
            }
            .addOnFailureListener { e ->
                _operationStatus.postValue("Gagal memuat data pengguna: ${e.message}")
            }
    }

    // Method untuk menyimpan avatar pengguna ke Firestore
    fun saveAvatarSelection(selectedAvatar: Int) {
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            _operationStatus.postValue("User tidak ditemukan. Silakan login kembali.")
            return
        }

        val userId = currentUser.uid
        firestore.collection("users").document(userId)
            .update("avatar", selectedAvatar)
            .addOnSuccessListener {
                _userAvatar.postValue(selectedAvatar)
                _operationStatus.postValue("Avatar berhasil diperbarui.")
            }
            .addOnFailureListener { e ->
                _operationStatus.postValue("Gagal memperbarui avatar: ${e.message}")
            }
    }

    // Method untuk mereset avatar ke default
    fun resetAvatarToDefault() {
        saveAvatarSelection(R.drawable.default_avatar)
    }
}
