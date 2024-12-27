package com.example.femlife.ui.activities.profile.avatar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.femlife.databinding.ItemAvatarBinding

class AvatarAdapter(
    private val avatars: List<Int>, // List of avatar resource IDs
    private val onAvatarSelected: (Int) -> Unit // Callback for selected avatar
) : RecyclerView.Adapter<AvatarAdapter.AvatarViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvatarViewHolder {
        val binding = ItemAvatarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AvatarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AvatarViewHolder, position: Int) {
        holder.bind(avatars[position])
    }

    override fun getItemCount(): Int = avatars.size

    inner class AvatarViewHolder(private val binding: ItemAvatarBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(avatarRes: Int) {
            binding.ivAvatarItem.setImageResource(avatarRes)
            binding.root.setOnClickListener {
                onAvatarSelected(avatarRes) // Trigger callback
            }
        }
    }
}
