package com.example.femlife.ui.fragments.home.workshop.edit

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.femlife.R
import com.example.femlife.data.workshop.WorkshopItem
import com.example.femlife.repository.WorkshopRepository
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class EditWorkshopActivity : AppCompatActivity() {

    private lateinit var etTitle: TextInputEditText
    private lateinit var etDescription: TextInputEditText
    private lateinit var etLink: TextInputEditText
    private lateinit var ivImagePreview: ImageView
    private lateinit var btnUploadImage: Button
    private lateinit var btnSubmit: Button
    private var imageUri: Uri? = null // Changed from `val` to `var` to allow reassignment

    private val workshopRepository = WorkshopRepository()
    private var workshopItem: WorkshopItem? = null // Holds the current workshop being edited

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_workshop)

        // Initialize views
        etTitle = findViewById(R.id.etTitle)
        etDescription = findViewById(R.id.etDescription)
        etLink = findViewById(R.id.etLink)
        ivImagePreview = findViewById(R.id.ivImagePreview)
        btnUploadImage = findViewById(R.id.btnUploadImage)
        btnSubmit = findViewById(R.id.btnSubmit)

        // Retrieve passed WorkshopItem
        workshopItem = intent.getParcelableExtra("workshopItem")

        if (workshopItem != null) {
            populateFields(workshopItem!!)
        } else {
            showToast("Data workshop tidak ditemukan")
            finish()
        }

        // Set up the toolbar
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Ubah Workshop"

        // Set up button actions
        btnUploadImage.setOnClickListener {
            openImagePicker()
        }

        btnSubmit.setOnClickListener {
            lifecycleScope.launch { // Launch coroutine to handle suspend calls
                updateWorkshop()
            }
        }
    }

    private fun populateFields(workshop: WorkshopItem) {
        etTitle.setText(workshop.title)
        etDescription.setText(workshop.description)
        etLink.setText(workshop.link)

        // Load image into preview (assuming you use a library like Glide or Picasso)
        Glide.with(this)
            .load(workshop.imageUrl)
            .into(ivImagePreview)
        ivImagePreview.visibility = ImageView.VISIBLE
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        imagePickerLauncher.launch(intent)
    }

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                imageUri = data?.data
                ivImagePreview.setImageURI(imageUri)
                ivImagePreview.visibility = ImageView.VISIBLE
            }
        }

    private suspend fun updateWorkshop() {
        val title = etTitle.text.toString()
        val description = etDescription.text.toString()
        val link = etLink.text.toString()

        if (title.isEmpty() || description.isEmpty() || link.isEmpty()) {
            showToast("Please fill all fields")
            return
        }

        // Update fields in the workshop object
        workshopItem?.apply {
            this.title = title
            this.description = description
            this.link = link
        }

        // If a new image is selected, upload it first
        if (imageUri != null) {
            try {
                val imageUrl = workshopRepository.uploadImage(this@EditWorkshopActivity, imageUri!!)
                workshopItem?.imageUrl = imageUrl
                saveWorkshop()
            } catch (e: Exception) {
                showToast("Error uploading image: ${e.message}")
            }
        } else {
            // If no new image, save workshop directly
            saveWorkshop()
        }
    }

    private suspend fun saveWorkshop() {
        try {
            workshopItem?.let {
                workshopRepository.updateWorkshop(it)
                showToast("Workshop berhasil diubah!")

                // Return the updated workshop to the previous activity
                val intent = Intent()
                intent.putExtra("updatedWorkshop", it)
                setResult(Activity.RESULT_OK, intent)

                finish() // Close the activity after successful update
            }
        } catch (e: Exception) {
            showToast("Error updating workshop: ${e.message}")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // Handle the back button press for the toolbar
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
