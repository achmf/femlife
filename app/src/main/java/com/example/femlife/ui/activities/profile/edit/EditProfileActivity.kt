package com.example.femlife.ui.activities.profile.edit

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.femlife.data.User
import com.example.femlife.databinding.ActivityEditProfileBinding
import java.text.SimpleDateFormat
import java.util.*

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private val viewModel: EditProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        binding.etDateOfBirth.setOnClickListener {
            showDatePicker()
        }

        binding.btnSave.setOnClickListener {
            saveUserData()
        }
    }

    private fun observeViewModel() {
        viewModel.userData.observe(this) { user ->
            binding.etName.setText(user.name)
            binding.etDateOfBirth.setText(user.dateOfBirth)
            binding.etLocation.setText(user.location)
            binding.etPhoneNumber.setText(user.phoneNumber)
            binding.etEmail.setText(user.email)
        }

        viewModel.operationStatus.observe(this) { status ->
            Toast.makeText(this, status, Toast.LENGTH_SHORT).show()
            if (status == "Data updated successfully") {
                finish()
            }
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(selectedYear, selectedMonth, selectedDay)
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            binding.etDateOfBirth.setText(dateFormat.format(selectedDate.time))
        }, year, month, day).show()
    }

    private fun saveUserData() {
        val name = binding.etName.text.toString().trim()
        val dateOfBirth = binding.etDateOfBirth.text.toString().trim()
        val location = binding.etLocation.text.toString().trim()
        val phoneNumber = binding.etPhoneNumber.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()

        if (name.isEmpty() || dateOfBirth.isEmpty() || location.isEmpty() || phoneNumber.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedUser = mapOf(
            "name" to name,
            "dateOfBirth" to dateOfBirth,
            "location" to location,
            "phoneNumber" to phoneNumber,
            "email" to email,
            "age" to calculateAge(dateOfBirth)
        )

        viewModel.updateUserData(updatedUser)
    }

    private fun calculateAge(dateOfBirth: String): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val birthDate = sdf.parse(dateOfBirth) ?: return ""
        val today = Calendar.getInstance()
        val birthCalendar = Calendar.getInstance()
        birthCalendar.time = birthDate
        var age = today.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR)
        if (today.get(Calendar.DAY_OF_YEAR) < birthCalendar.get(Calendar.DAY_OF_YEAR)) {
            age--
        }
        return age.toString()
    }
}
