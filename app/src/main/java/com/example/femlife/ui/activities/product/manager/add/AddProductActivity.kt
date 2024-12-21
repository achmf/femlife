package com.example.femlife.ui.activities.product.manager.add

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.femlife.data.product.Product
import com.example.femlife.databinding.ActivityAddProductBinding
import com.example.femlife.repository.ProductRepository
import kotlinx.coroutines.launch
import java.util.UUID

class AddProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddProductBinding
    private val productRepository = ProductRepository()
    private var imageUri: Uri? = null

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imageUri = it
            Glide.with(this).load(uri).into(binding.ivProductImage)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupListeners()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Tambah Produk"

        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupListeners() {
        binding.btnUploadImage.setOnClickListener {
            getContent.launch("image/*")
        }

        binding.btnAddProduct.setOnClickListener {
            val productName = binding.etProductName.text.toString().trim()
            val productPrice = binding.etProductPrice.text.toString().trim()
            val adminWhatsApp = binding.etAdminWhatsApp.text.toString().trim()

            if (productName.isEmpty() || productPrice.isEmpty() || adminWhatsApp.isEmpty()) {
                Toast.makeText(this, "Semua kolom harus diisi", Toast.LENGTH_SHORT).show()
            } else {
                val price = productPrice.toDoubleOrNull()
                if (price == null) {
                    Toast.makeText(this, "Harga produk tidak valid", Toast.LENGTH_SHORT).show()
                } else {
                    addProduct(productName, price, adminWhatsApp)
                }
            }
        }
    }

    private fun addProduct(name: String, price: Double, adminWhatsApp: String) {
        lifecycleScope.launch {
            try {
                val imageUrl = imageUri?.let { productRepository.uploadImage(this@AddProductActivity, it) } ?: ""
                val newProduct = Product(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    image = imageUrl,
                    price = price,
                    adminWhatsApp = adminWhatsApp
                )
                productRepository.addProduct(newProduct)
                Toast.makeText(
                    this@AddProductActivity,
                    "Produk $name berhasil ditambahkan dengan harga Rp$price!",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            } catch (e: Exception) {
                Toast.makeText(
                    this@AddProductActivity,
                    "Gagal menambahkan produk: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
