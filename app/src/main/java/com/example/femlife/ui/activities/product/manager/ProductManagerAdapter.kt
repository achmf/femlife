package com.example.femlife.ui.activities.product.manager

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.femlife.R
import com.example.femlife.data.product.Product
import com.example.femlife.databinding.ItemProductManagerBinding
import com.bumptech.glide.Glide

class ProductManagerAdapter(
    private val onEdit: (Product) -> Unit,
    private val onDelete: (Product) -> Unit
) : ListAdapter<Product, ProductManagerAdapter.ProductManagerViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductManagerViewHolder {
        val binding = ItemProductManagerBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ProductManagerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductManagerViewHolder, position: Int) {
        val product = getItem(position)
        holder.bind(product)
    }

    inner class ProductManagerViewHolder(private val binding: ItemProductManagerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.textProductName.text = product.name
            binding.textProductPrice.text = "Rp${product.price}"
            Glide.with(binding.root.context)
                .load(product.image)
                .error(R.drawable.ic_shop)
                .into(binding.imageProduct)

            binding.buttonOptions.setOnClickListener { view ->
                val popup = PopupMenu(view.context, view)
                popup.inflate(R.menu.menu_product_manager_item)
                popup.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.action_edit_product -> {
                            onEdit(product)
                            true
                        }
                        R.id.action_delete_product -> {
                            onDelete(product)
                            true
                        }
                        else -> false
                    }
                }
                popup.show()
            }
        }
    }

    class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
}

