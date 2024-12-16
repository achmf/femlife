package com.example.femlife.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.femlife.R
import com.example.femlife.databinding.ActivityMainBinding
import com.example.femlife.ui.activities.auth.LoginActivity
import com.example.femlife.ui.activities.profile.ProfileActivity
import com.example.femlife.ui.fragments.home.HomeFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            navigateToLoginActivity()
            return
        }

        // Periksa apakah user sudah menyelesaikan profile setup
        val userId = currentUser.uid
        firestore.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val isProfileCompleted = document.getBoolean("isProfileCompleted") ?: false
                    if (isProfileCompleted) {
                        // User sudah menyelesaikan setup, lanjutkan ke MainActivity
                        binding = ActivityMainBinding.inflate(layoutInflater)
                        setContentView(binding.root)

                        setupUI(savedInstanceState)
                    } else {
                        // User belum menyelesaikan setup, arahkan ke ProfileSetupActivity
                        navigateToProfileSetupActivity()
                    }
                } else {
                    // Data user tidak ditemukan, kembali ke LoginActivity
                    navigateToLoginActivity()
                }
            }
            .addOnFailureListener {
                // Jika terjadi kesalahan saat memuat data, kembali ke LoginActivity
                navigateToLoginActivity()
            }
    }

    private fun setupUI(savedInstanceState: Bundle?) {
        // Navigasi ke ProfileActivity saat profile icon diklik
        binding.toolbar.findViewById<android.widget.ImageView>(R.id.iv_profile_icon).setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        // Bottom Navigation Listener
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> loadFragment(HomeFragment())
                else -> false
            }
        }

        // Default Fragment
        if (savedInstanceState == null) {
            binding.bottomNavigation.selectedItemId = R.id.nav_home
        }
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun navigateToProfileSetupActivity() {
        val intent = Intent(this, ProfileSetupActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun loadFragment(fragment: Fragment): Boolean {
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, fragment)
            .commit()
        return true
    }
}
