package com.example.femlife.ui.activities.product.manager

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.femlife.R
import com.example.femlife.data.product.Product
import com.example.femlife.databinding.ActivityProductManagerBinding
import com.example.femlife.repository.ProductRepository
import com.example.femlife.ui.activities.product.manager.add.AddProductActivity
import com.example.femlife.ui.activities.product.manager.edit.EditProductActivity
import kotlinx.coroutines.launch

class ProductManagerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductManagerBinding
    private lateinit var productAdapter: ProductManagerAdapter
    private val productRepository = ProductRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        loadProducts()
    }

    // Memuat ulang data produk setiap kali aktivitas kembali tampil
    override fun onResume() {
        super.onResume()
        loadProducts() // Pastikan data selalu terupdate setelah menambah atau mengedit produk
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Kelola Produk"

        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        productAdapter = ProductManagerAdapter(
            onEdit = { product -> navigateToEditProduct(product) },
            onDelete = { product -> deleteProduct(product) }
        )
        binding.recyclerViewProducts.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewProducts.adapter = productAdapter
    }

    private fun loadProducts() {
        lifecycleScope.launch {
            try {
                val products = productRepository.getAllProducts()
                productAdapter.submitList(products)
            } catch (e: Exception) {
                Toast.makeText(this@ProductManagerActivity, "Failed to load products: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToEditProduct(product: Product) {
        val intent = Intent(this, EditProductActivity::class.java)
        intent.putExtra("productId", product.id)
        startActivity(intent)
    }

    private fun deleteProduct(product: Product) {
        lifecycleScope.launch {
            try {
                productRepository.deleteProduct(product.id)
                loadProducts() // Reload the list after deletion
                Toast.makeText(this@ProductManagerActivity, "Product deleted successfully", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@ProductManagerActivity, "Failed to delete product: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_product_manager_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add_product -> {
                navigateToAddProduct()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun navigateToAddProduct() {
        val intent = Intent(this, AddProductActivity::class.java)
        startActivity(intent)
    }
}
