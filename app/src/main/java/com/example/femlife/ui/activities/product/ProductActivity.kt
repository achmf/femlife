package com.example.femlife.ui.activities.product

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.femlife.R
import com.example.femlife.databinding.ActivityProductBinding
import com.example.femlife.repository.ProductRepository
import com.example.femlife.ui.activities.product.manager.ProductManagerActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class ProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductBinding
    private lateinit var productAdapter: ProductAdapter
    private val productRepository = ProductRepository()
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var userRole: String? = null // Role akan diambil dari Firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fetchUserRole { role ->
            userRole = role
            setupToolbar()
        }

        setupRecyclerView()
        loadProducts()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Produk"

        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        if (userRole == "user") {
            binding.toolbar.menu.clear() // Hapus menu untuk user biasa
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (userRole == "admin") {
            menuInflater.inflate(R.menu.menu_product, menu) // Menu hanya untuk admin
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add_product -> {
                navigateToProductManager()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter(emptyList())
        binding.recyclerViewProducts.layoutManager = GridLayoutManager(this, 2)
        binding.recyclerViewProducts.adapter = productAdapter
    }

    private fun loadProducts() {
        lifecycleScope.launch {
            try {
                val products = productRepository.getAllProducts()
                productAdapter.updateProducts(products)
            } catch (e: Exception) {
                // Handle error (e.g., show a toast or a snackbar)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadProducts() // Refresh products saat activity resume
    }

    private fun navigateToProductManager() {
        val intent = Intent(this, ProductManagerActivity::class.java)
        startActivity(intent)
    }

    private fun fetchUserRole(callback: (String?) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            firestore.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    val role = document.getString("role")
                    callback(role)
                }
                .addOnFailureListener {
                    callback(null) // Role tidak ditemukan
                }
        } else {
            callback(null) // User tidak login
        }
    }
}
