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
import kotlinx.coroutines.launch

class ProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductBinding
    private lateinit var productAdapter: ProductAdapter
    private val productRepository = ProductRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        loadProducts()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Products"

        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
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
        // Reload the products when the activity is resumed
        loadProducts()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_product, menu)
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

    private fun navigateToProductManager() {
        val intent = Intent(this, ProductManagerActivity::class.java)
        startActivity(intent)
    }
}
