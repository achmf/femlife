package com.example.femlife.ui.activities.product

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.femlife.data.product.Product
import com.example.femlife.databinding.DialogQuantityPickerBinding
import com.example.femlife.databinding.ItemProductBinding
import com.bumptech.glide.Glide
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.text.NumberFormat
import java.util.Locale

class ProductAdapter(private var productList: List<Product>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(private val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.productName.text = product.name
            binding.productPrice.text = formatRupiah(product.price)
            Glide.with(binding.root.context)
                .load(product.image)
                .error(com.example.femlife.R.drawable.ic_shop)
                .into(binding.productImage)

            binding.buyButton.setOnClickListener {
                showQuantityDialog(binding.root.context, product)
            }
        }

        private fun showQuantityDialog(context: Context, product: Product) {
            // Inflate the dialog layout
            val dialogBinding = DialogQuantityPickerBinding.inflate(LayoutInflater.from(context))
            val dialog = AlertDialog.Builder(context)
                .setView(dialogBinding.root)
                .create()

            // Set product name dynamically
            dialogBinding.productNameText.text = product.name

            var quantity = 1 // Default quantity
            updateTotalPrice(dialogBinding, product.price, quantity)

            // Set up button listeners
            dialogBinding.decreaseQuantityButton.setOnClickListener {
                if (quantity > 1) {
                    quantity--
                    dialogBinding.quantityText.text = quantity.toString()
                    updateTotalPrice(dialogBinding, product.price, quantity)
                }
            }

            dialogBinding.increaseQuantityButton.setOnClickListener {
                quantity++
                dialogBinding.quantityText.text = quantity.toString()
                updateTotalPrice(dialogBinding, product.price, quantity)
            }

            dialogBinding.orderNowButton.setOnClickListener {
                val totalPrice = product.price * quantity
                openWhatsApp(context, product, quantity, totalPrice)
                dialog.dismiss()
            }

            dialog.show()
        }

        private fun openWhatsApp(context: Context, product: Product, quantity: Int, totalPrice: Double) {
            val message = """
                Hello admin, 
                Saya ingin memesan ${quantity}x produk "${product.name}" 
                dengan total harga ${formatRupiah(totalPrice)}.
            """.trimIndent()
            val encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8.toString())
            val url = "https://wa.me/${product.adminWhatsApp}/?text=$encodedMessage"

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        }

        private fun updateTotalPrice(dialogBinding: DialogQuantityPickerBinding, price: Double, quantity: Int) {
            val totalPrice = price * quantity
            dialogBinding.totalPriceText.text = "Total: ${formatRupiah(totalPrice)}"
        }

        private fun formatRupiah(price: Double): String {
            val format = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
            format.maximumFractionDigits = 0
            return format.format(price)
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
