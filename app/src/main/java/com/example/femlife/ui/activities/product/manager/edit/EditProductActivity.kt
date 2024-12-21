package com.example.femlife.ui.activities.product.manager.edit

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.femlife.data.product.Product
import com.example.femlife.databinding.ActivityEditProductBinding
import com.example.femlife.repository.ProductRepository
import kotlinx.coroutines.launch

class EditProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProductBinding
    private val productRepository = ProductRepository()
    private var productId: String? = null
    private var imageUri: Uri? = null
    private var currentImageUrl: String? = null

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imageUri = it
            Glide.with(this).load(uri).into(binding.ivProductImage)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        productId = intent.getStringExtra("productId")
        loadProductData()
        setupListeners()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Edit Produk"

        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun loadProductData() {
        productId?.let { id ->
            lifecycleScope.launch {
                try {
                    val product = productRepository.getProduct(id)
                    product?.let {
                        binding.etProductName.setText(it.name)
                        binding.etProductPrice.setText(it.price.toString())
                        binding.etAdminWhatsApp.setText(it.adminWhatsApp)
                        currentImageUrl = it.image
                        Glide.with(this@EditProductActivity).load(it.image).into(binding.ivProductImage)
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@EditProductActivity, "Failed to load product: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupListeners() {
        binding.btnUploadImage.setOnClickListener {
            getContent.launch("image/*")
        }

        binding.btnUpdateProduct.setOnClickListener {
            val name = binding.etProductName.text.toString().trim()
            val price = binding.etProductPrice.text.toString().trim().toDoubleOrNull()
            val adminWhatsApp = binding.etAdminWhatsApp.text.toString().trim()

            if (name.isEmpty() || price == null || adminWhatsApp.isEmpty()) {
                Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show()
            } else {
                updateProduct(name, price, adminWhatsApp)
            }
        }
    }

    private fun updateProduct(name: String, price: Double, adminWhatsApp: String) {
        productId?.let { id ->
            lifecycleScope.launch {
                try {
                    val imageUrl = if (imageUri != null) {
                        productRepository.uploadImage(this@EditProductActivity, imageUri!!)
                    } else {
                        currentImageUrl
                    }
                    val updatedProduct = Product(id, name, imageUrl ?: "", price, adminWhatsApp)
                    productRepository.updateProduct(updatedProduct)
                    Toast.makeText(this@EditProductActivity, "Product updated successfully", Toast.LENGTH_SHORT).show()
                    finish()
                } catch (e: Exception) {
                    Toast.makeText(this@EditProductActivity, "Failed to update product: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}