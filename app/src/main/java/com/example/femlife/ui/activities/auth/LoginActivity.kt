package com.example.femlife.ui.activities.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.femlife.R
import com.example.femlife.ui.activities.MainActivity
import com.example.femlife.utils.FirebaseAuthHelper
import com.google.android.gms.common.SignInButton

class LoginActivity : AppCompatActivity() {

    private lateinit var authHelper: FirebaseAuthHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        // Inisialisasi helper
        authHelper = FirebaseAuthHelper(
            this,
            onAuthSuccess = { navigateToMainActivity() },
            onAuthFailure = { /* Optional: Handle failure here */ }
        )

        // Konfigurasi Google Sign-In
        authHelper.configureGoogleSignIn(getString(R.string.default_web_client_id))

        // Set listener tombol Google Sign-In
        val googleSignInButton: SignInButton = findViewById(R.id.btn_google_sign_in)
        googleSignInButton.setOnClickListener {
            authHelper.launchSignIn()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        authHelper.handleSignInResult(requestCode, data)
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
