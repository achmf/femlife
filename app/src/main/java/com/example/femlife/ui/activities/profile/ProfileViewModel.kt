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

    val avatars = listOf(
        R.drawable.ava1,
        R.drawable.ava2,
        R.drawable.ava3,
        R.drawable.ava4,
        R.drawable.ava5,
        R.drawable.ava6,
        R.drawable.ava7,
        R.drawable.ava8,
        R.drawable.ava9,
        R.drawable.ava10,
        R.drawable.ava11,
        R.drawable.ava12,
        R.drawable.ava13,
        R.drawable.ava14,
        R.drawable.ava15,
        R.drawable.ava16,
        R.drawable.ava17,
        R.drawable.ava18,
        R.drawable.ava19,
        R.drawable.ava20,
    )

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

    private val _operationStatus = MutableLiveData<String>()
    val operationStatus: LiveData<String> get() = _operationStatus

    private val _userRole = MutableLiveData<String>()
    val userRole: LiveData<String> get() = _userRole

    /**
     * Fetches user data from Firestore and updates LiveData fields.
     */
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
                    _userAvatar.postValue(avatarResId ?: R.drawable.default_avatar)

                    _userRole.postValue(document.getString("role") ?: "user")
                    _operationStatus.postValue("Data pengguna berhasil dimuat.")
                } else {
                    _operationStatus.postValue("Data pengguna tidak ditemukan.")
                }
            }
            .addOnFailureListener { e ->
                _operationStatus.postValue("Gagal memuat data pengguna: ${e.message}")
            }
    }

    /**
     * Updates the user's role in Firestore and LiveData.
     *
     * @param newRole The new role to set (e.g., "user" or "admin").
     */
    fun updateUserRole(newRole: String) {
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            _operationStatus.postValue("User tidak ditemukan. Silakan login kembali.")
            return
        }

        val userId = currentUser.uid
        firestore.collection("users").document(userId)
            .update("role", newRole)
            .addOnSuccessListener {
                _userRole.postValue(newRole)
                _operationStatus.postValue("Role berhasil diperbarui menjadi '$newRole'.")
            }
            .addOnFailureListener { e ->
                _operationStatus.postValue("Gagal memperbarui role: ${e.message}")
            }
    }

    /**
     * Saves the selected avatar in Firestore and updates LiveData.
     *
     * @param avatarResId The resource ID of the selected avatar.
     */
    fun saveAvatarSelection(avatarResId: Int) {
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            _operationStatus.postValue("User tidak ditemukan. Silakan login kembali.")
            return
        }

        val userId = currentUser.uid
        firestore.collection("users").document(userId)
            .update("avatar", avatarResId)
            .addOnSuccessListener {
                _userAvatar.postValue(avatarResId)
                _operationStatus.postValue("Avatar berhasil disimpan.")
            }
            .addOnFailureListener { e ->
                _operationStatus.postValue("Gagal menyimpan avatar: ${e.message}")
            }
    }

    /**
     * Resets the user's avatar to the default avatar.
     */
    fun resetAvatarToDefault() {
        saveAvatarSelection(R.drawable.default_avatar)
    }
}
