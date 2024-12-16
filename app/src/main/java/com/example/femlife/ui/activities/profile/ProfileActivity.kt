package com.example.femlife.ui.activities.profile

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.femlife.databinding.ActivityProfileBinding
import com.example.femlife.ui.activities.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handle back button
        binding.ivBackButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Handle logout button
        binding.btnLogout.setOnClickListener {
            logoutUser()
        }

        // Fetch and display user data
        fetchUserData()
    }

    private fun fetchUserData() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "User tidak ditemukan. Silakan login kembali.", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = currentUser.uid
        firestore.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    binding.tvUserName.text = "${document.getString("name")}"
                    binding.tvUserAge.text = "${document.getString("age")} Tahun"
                    binding.tvUserBirthplace.text = "${document.getString("dateOfBirth")}"
                    binding.tvUserLocation.text = "${document.getString("location")}"
                    binding.tvUserPhone.text = "${document.getString("phoneNumber")}"
                    binding.tvUserEmail.text = "${document.getString("email")}"
                } else {
                    Toast.makeText(this, "Data pengguna tidak ditemukan.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal memuat data pengguna: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun logoutUser() {
        firebaseAuth.signOut()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
