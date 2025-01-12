package com.example.femlife.ui.activities.splashscreen

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.femlife.R
import com.example.femlife.ui.activities.auth.LoginActivity
import com.example.femlife.ui.activities.MainActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set splash screen theme
        setTheme(R.style.Theme_Femlife_SplashScreen)

        // Use a delay to show the splash screen for a few seconds before navigating
        Handler().postDelayed({
            // Check if user is already logged in using FirebaseAuth
            val currentUser = FirebaseAuth.getInstance().currentUser

            if (currentUser == null) {
                // If no user is logged in, navigate to LoginActivity
                navigateToLoginActivity()
            } else {
                // If user is logged in, navigate to MainActivity
                navigateToMainActivity()
            }
        }, 2000) // Delay in milliseconds (2000ms = 2 seconds)
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()  // Close SplashActivity so the user cannot go back to it
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()  // Close SplashActivity so the user cannot go back to it
    }
}