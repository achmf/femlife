package com.example.femlife.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.femlife.R
import com.example.femlife.databinding.ActivityMainBinding
import com.example.femlife.ui.activities.auth.LoginActivity
import com.example.femlife.ui.activities.overview.ProfileSetupActivity
import com.example.femlife.ui.activities.profile.ProfileActivity
import com.example.femlife.ui.fragments.femtalk.FemTalkFragment
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
                    val avatarResourceId = document.getLong("avatar")?.toInt() ?: R.drawable.default_avatar

                    if (isProfileCompleted) {
                        binding = ActivityMainBinding.inflate(layoutInflater)
                        setContentView(binding.root)

                        // Set the avatar from Firestore
                        binding.toolbar.findViewById<ImageView>(R.id.iv_profile_icon).setImageResource(avatarResourceId)

                        setupUI(savedInstanceState)
                    } else {
                        navigateToProfileSetupActivity()
                    }
                } else {
                    navigateToLoginActivity()
                }
            }
            .addOnFailureListener {
                navigateToLoginActivity()
            }
    }

    private fun setupUI(savedInstanceState: Bundle?) {
        // Navigasi ke ProfileActivity saat profile icon diklik
        binding.toolbar.findViewById<ImageView>(R.id.iv_profile_icon).setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        // Bottom Navigation Listener
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> loadFragment(HomeFragment())
                R.id.nav_message -> loadFragment(FemTalkFragment())
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
