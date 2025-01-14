package com.example.femlife.ui.fragments.home.workshop.create

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.femlife.R
import com.example.femlife.data.workshop.WorkshopItem
import com.example.femlife.repository.WorkshopRepository
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class CreateWorkshopActivity : AppCompatActivity() {

    private lateinit var etTitle: TextInputEditText
    private lateinit var etDescription: TextInputEditText
    private lateinit var etLink: TextInputEditText
    private lateinit var ivImagePreview: ImageView
    private lateinit var btnUploadImage: Button
    private lateinit var btnSubmit: Button
    private var imageUri: Uri? = null

    private val workshopRepository = WorkshopRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_workshop)

        // Set up the toolbar
        setupToolbar()

        // Initialize views
        etTitle = findViewById(R.id.etTitle)
        etDescription = findViewById(R.id.etDescription)
        etLink = findViewById(R.id.etLink)
        ivImagePreview = findViewById(R.id.ivImagePreview)
        btnUploadImage = findViewById(R.id.btnUploadImage)
        btnSubmit = findViewById(R.id.btnSubmit)

        // Set up button actions
        btnUploadImage.setOnClickListener {
            openImagePicker()
        }

        btnSubmit.setOnClickListener {
            submitWorkshop()
        }
    }

    private fun setupToolbar() {
        // Set the toolbar as the app's action bar
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Create Workshop"

        // Handle the back button click in the toolbar
        findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar).setNavigationOnClickListener {
            onBackPressed() // Go back to the previous activity
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        imagePickerLauncher.launch(intent)
    }

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                imageUri = data?.data
                ivImagePreview.setImageURI(imageUri)
                ivImagePreview.visibility = ImageView.VISIBLE
            }
        }

    private fun submitWorkshop() {
        val title = etTitle.text.toString()
        val description = etDescription.text.toString()
        val link = etLink.text.toString()

        if (title.isEmpty() || description.isEmpty() || link.isEmpty() || imageUri == null) {
            showToast("Please fill all fields and upload an image")
            return
        }

        // Create a workshop item object with empty image URL for now
        val workshopItem = WorkshopItem(
            imageUrl = "", // Will be updated after image upload
            title = title,
            description = description,
            link = link
        )

        // Upload image and submit data asynchronously
        imageUri?.let { uri ->
            lifecycleScope.launch {
                try {
                    // Upload image and get URL
                    val imageUrl = workshopRepository.uploadImage(this@CreateWorkshopActivity, uri)
                    // Update the image URL in the workshop item
                    workshopItem.imageUrl = imageUrl
                    // Save the workshop to Firestore
                    saveWorkshop(workshopItem)
                } catch (e: Exception) {
                    showToast("Error uploading image or saving workshop: ${e.message}")
                }
            }
        }
    }

    private suspend fun saveWorkshop(workshopItem: WorkshopItem) {
        try {
            // Add workshop to Firestore
            workshopRepository.addWorkshop(workshopItem)
            showToast("Workshop created successfully!")

            // Return result to HomeFragment
            val intent = Intent()
            intent.putExtra("newWorkshop", workshopItem)  // Send the created workshop back
            setResult(RESULT_OK, intent)

            finish() // Close the activity after successful submission
        } catch (e: Exception) {
            showToast("Error creating workshop: ${e.message}")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
