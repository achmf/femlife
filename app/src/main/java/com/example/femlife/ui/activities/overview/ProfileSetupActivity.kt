package com.example.femlife.ui.activities.overview

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.femlife.data.User
import com.example.femlife.databinding.ActivityProfileSetupBinding
import com.example.femlife.ui.activities.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class ProfileSetupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileSetupBinding
    private val firestore = FirebaseFirestore.getInstance() // Instance Firestore
    private val firebaseAuth = FirebaseAuth.getInstance() // Instance FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileSetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handle Tanggal Lahir
        binding.etDateOfBirth.setOnClickListener {
            showDatePicker()
        }

        // Handle Tombol Submit
        binding.btnSubmit.setOnClickListener {
            handleSubmit()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            // Format Tanggal
            val date = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            binding.etDateOfBirth.setText(date)

            // Hitung Umur
            calculateAge(selectedYear, selectedMonth, selectedDay)
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun calculateAge(year: Int, month: Int, day: Int) {
        val dob = Calendar.getInstance()
        dob.set(year, month, day)

        val today = Calendar.getInstance()
        var age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--
        }

        binding.etAge.setText(age.toString())
    }

    private fun handleSubmit() {
        val name = binding.etName.text.toString().trim()
        val dateOfBirth = binding.etDateOfBirth.text.toString().trim()
        val location = binding.etLocation.text.toString().trim()
        val phoneNumber = binding.etPhoneNumber.text.toString().trim()
        val age = binding.etAge.text.toString().trim()

        if (name.isEmpty() || location.isEmpty() || dateOfBirth.isEmpty() || phoneNumber.isEmpty() || age.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi!", Toast.LENGTH_SHORT).show()
            return
        }

        // Dapatkan UID dan email user saat ini
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "User tidak ditemukan. Silakan login kembali.", Toast.LENGTH_SHORT).show()
            return
        }
        val userId = currentUser.uid
        val email = currentUser.email ?: ""

        // Buat objek User
        val user = User(
            name = name,
            dateOfBirth = dateOfBirth,
            location = location,
            phoneNumber = phoneNumber,
            age = age,
            email = email,
            isProfileCompleted = true // Tandai bahwa user telah menyelesaikan setup
        )

        // Simpan data user ke Firestore
        firestore.collection("users").document(userId)
            .set(user)
            .addOnSuccessListener {
                Toast.makeText(this, "Data berhasil disimpan!", Toast.LENGTH_SHORT).show()

                // Lanjutkan ke MainActivity
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal menyimpan data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}