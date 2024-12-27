package com.example.femlife.ui.activities.profile

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.femlife.R
import com.example.femlife.databinding.ActivityProfileBinding
import com.example.femlife.ui.activities.auth.LoginActivity
import com.example.femlife.ui.activities.profile.avatar.AvatarAdapter
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var viewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

        // Observasi data pengguna dari ViewModel
        setupObservers()

        // Handle back button
        binding.ivBackButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Handle logout button
        binding.btnLogout.setOnClickListener {
            logoutUser()
        }

        // Handle profile picture click
        binding.ivProfilePicture.setOnClickListener {
            showAvatarPickerDialog()
        }

        // Fetch data pengguna
        viewModel.fetchUserData()
    }

    private fun setupObservers() {
        viewModel.userName.observe(this) {
            binding.tvUserName.text = it
        }
        viewModel.userAge.observe(this) {
            binding.tvUserAge.text = it
        }
        viewModel.userBirthplace.observe(this) {
            binding.tvUserBirthplace.text = it
        }
        viewModel.userLocation.observe(this) {
            binding.tvUserLocation.text = it
        }
        viewModel.userPhone.observe(this) {
            binding.tvUserPhone.text = it
        }
        viewModel.userEmail.observe(this) {
            binding.tvUserEmail.text = it
        }
        viewModel.userAvatar.observe(this) {
            binding.ivProfilePicture.setImageResource(it)
        }
        viewModel.operationStatus.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showAvatarPickerDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_avatar_picker)

        val recyclerView = dialog.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rv_avatar_list)
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        recyclerView.adapter = AvatarAdapter(viewModel.avatars) { selectedAvatar ->
            viewModel.saveAvatarSelection(selectedAvatar) // Update avatar via ViewModel
            dialog.dismiss()
        }

        val resetButton = dialog.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_reset_avatar)
        resetButton.setOnClickListener {
            viewModel.resetAvatarToDefault() // Reset avatar via ViewModel
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun logoutUser() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}