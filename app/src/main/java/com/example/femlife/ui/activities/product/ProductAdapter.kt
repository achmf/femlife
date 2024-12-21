package com.example.femlife.ui.activities.product

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.femlife.data.product.Product
import com.example.femlife.databinding.ItemProductBinding
import com.bumptech.glide.Glide
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class ProductAdapter(private var productList: List<Product>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(private val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.productName.text = product.name
            binding.productPrice.text = "Rp${product.price}"
            Glide.with(binding.root.context)
                .load(product.image)
                .error(com.example.femlife.R.drawable.ic_shop)
                .into(binding.productImage)

            binding.buyButton.setOnClickListener {
                openWhatsApp(product)
            }
        }

        private fun openWhatsApp(product: Product) {
            val context = binding.root.context
            val message = "Hello admin, saya ingin membeli produk ${product.name} dengan harga Rp${product.price}"
            val encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8.toString())
            val url = "https://wa.me/${product.adminWhatsApp}/?text=$encodedMessage"

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(productList[position])
    }

    override fun getItemCount(): Int = productList.size

    fun updateProducts(newProducts: List<Product>) {
        productList = newProducts
        notifyDataSetChanged()
    }
}
