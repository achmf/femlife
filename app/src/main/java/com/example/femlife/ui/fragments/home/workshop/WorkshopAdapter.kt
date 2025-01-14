package com.example.femlife.ui.fragments.home.workshop

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.femlife.R
import com.example.femlife.data.workshop.WorkshopItem

class WorkshopAdapter(
    private var workshopItems: List<WorkshopItem>,
    private val onItemClick: (WorkshopItem) -> Unit,
    private val onItemLongClick: (WorkshopItem, String) -> Unit // Callback for long-press actions
) : RecyclerView.Adapter<WorkshopAdapter.WorkshopViewHolder>() {

    inner class WorkshopViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageWorkshop: ImageView = view.findViewById(R.id.image_workshop)
        val textTitle: TextView = view.findViewById(R.id.text_workshop_title)
        val textDescription: TextView = view.findViewById(R.id.text_workshop_description)
        val textLink: TextView = view.findViewById(R.id.text_workshop_link)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkshopViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_workshop_card, parent, false)
        return WorkshopViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkshopViewHolder, position: Int) {
        val workshopItem = workshopItems[position]

        // Bind data
        holder.textTitle.text = workshopItem.title
        holder.textDescription.text = workshopItem.description
        holder.textLink.text = "Selengkapnya"

        // Load image using Glide
        Glide.with(holder.itemView.context)
            .load(workshopItem.imageUrl)
            .into(holder.imageWorkshop)

        // Handle click on card
        holder.itemView.setOnClickListener {
            onItemClick(workshopItem)
        }

        // Handle long-press on card
        // Handle long-press on card
        holder.itemView.setOnLongClickListener {
            val context = it.context
            val popupMenu = android.widget.PopupMenu(context, it)
            popupMenu.inflate(R.menu.workshop_context_menu) // Inflate custom menu

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_edit -> {
                        // Notify "Edit" action
                        onItemLongClick(workshopItem, "edit")
                        true
                    }
                    R.id.action_delete -> {
                        // Notify "Delete" action
                        onItemLongClick(workshopItem, "delete")
                        true
                    }
                    else -> false
                }
            }

            popupMenu.show()
            true // Indicate that the long click was handled
        }
    }

    override fun getItemCount(): Int = workshopItems.size

    fun updateWorkshopItems(newItems: List<WorkshopItem>) {
        workshopItems = newItems
        notifyDataSetChanged()
    }
}
