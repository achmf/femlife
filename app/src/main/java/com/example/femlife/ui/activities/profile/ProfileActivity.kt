package com.example.femlife.ui.activities.profile

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Window
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.femlife.R
import com.example.femlife.databinding.ActivityProfileBinding
import com.example.femlife.ui.activities.auth.LoginActivity
import com.example.femlife.ui.activities.profile.avatar.AvatarAdapter
import com.example.femlife.ui.activities.profile.edit.EditProfileActivity
import com.google.android.material.chip.Chip
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var viewModel: ProfileViewModel

    private var isLongPressed = false // To track long press for profile picture

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

        // Observe user data
        setupObservers()

        // Handle back button
        binding.ivBackButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Handle logout button
        binding.btnLogout.setOnClickListener {
            logoutUser()
        }

        // Handle profile picture click & long press
        binding.ivProfilePicture.setOnClickListener {
            if (!isLongPressed) {
                showAvatarPickerDialog()
            }
        }
        binding.ivProfilePicture.setOnLongClickListener {
            handleProfilePictureLongPress()
            true
        }

        // Handle edit profile click
        binding.tvEditProfile.setOnClickListener {
            navigateToEditProfile()
        }

        // Fetch user data
        viewModel.fetchUserData()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchUserData() // Reload latest data
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
        viewModel.userAvatar.observe(this) { avatarResId ->
            if (avatarResId != binding.ivProfilePicture.tag) {
                binding.ivProfilePicture.setImageResource(avatarResId)
                binding.ivProfilePicture.tag = avatarResId
            }
        }
        viewModel.operationStatus.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
        viewModel.userRole.observe(this) { role ->
            Toast.makeText(this, "Role saat ini: $role", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showAvatarPickerDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_avatar_picker)

        val recyclerView = dialog.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rv_avatar_list)
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        recyclerView.adapter = AvatarAdapter(viewModel.avatars) { selectedAvatar ->
            viewModel.saveAvatarSelection(selectedAvatar)
            dialog.dismiss()
        }

        val resetButton = dialog.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_reset_avatar)
        resetButton.setOnClickListener {
            viewModel.resetAvatarToDefault()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun handleProfilePictureLongPress() {
        isLongPressed = true
        Handler(Looper.getMainLooper()).postDelayed({
            isLongPressed = false // Reset long press state
        }, 1000) // Reset after 1 second

        val inputDialog = AlertDialog.Builder(this)
        val inputField = EditText(this)
        inputField.hint = "Masukkan password"
        inputDialog.setView(inputField)

        inputDialog.setPositiveButton("Submit") { _, _ ->
            val inputText = inputField.text.toString()
            if (inputText == "admin123") {
                showRoleToggleDialog()
            } else {
                Toast.makeText(this, "Password salah", Toast.LENGTH_SHORT).show()
            }
        }

        inputDialog.setNegativeButton("Batal", null)
        inputDialog.show()
    }

    private fun showRoleToggleDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_toggle_role)

        val switchRole = dialog.findViewById<SwitchMaterial>(R.id.switch_role)

        // Set initial state of the switch based on the current role
        viewModel.userRole.value?.let { role ->
            switchRole.isChecked = role == "admin"
        }

        // Listener untuk switch
        switchRole.setOnCheckedChangeListener { _, isChecked ->
            val newRole = if (isChecked) "admin" else "user"
            viewModel.updateUserRole(newRole) // Update role di ViewModel
        }

        dialog.show()
    }

    private fun navigateToEditProfile() {
        val intent = Intent(this, EditProfileActivity::class.java)
        startActivity(intent)
    }

    private fun logoutUser() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
