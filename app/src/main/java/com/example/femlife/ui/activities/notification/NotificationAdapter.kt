package com.example.femlife.ui.activities.notification

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.femlife.R
import com.example.femlife.data.notification.NotificationData
import com.example.femlife.data.notification.NotificationType
import java.text.SimpleDateFormat
import java.util.*

class NotificationAdapter(private var notifications: List<NotificationData>) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    class NotificationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.imageViewIcon)
        val title: TextView = view.findViewById(R.id.textViewTitle)
        val description: TextView = view.findViewById(R.id.textViewDescription)
        val time: TextView = view.findViewById(R.id.textViewTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notifications[position]
        holder.icon.setImageResource(getIconForNotificationType(notification.type))
        holder.title.text = notification.title
        holder.description.text = notification.message
        holder.time.text = formatDate(notification.timestamp)
    }

    override fun getItemCount() = notifications.size

    fun updateNotifications(newNotifications: List<NotificationData>) {
        notifications = newNotifications
        notifyDataSetChanged()
    }

    fun getNotificationAt(position: Int): NotificationData {
        return notifications[position]
    }

    private fun getIconForNotificationType(type: NotificationType): Int {
        return when (type) {
            NotificationType.PRE_MENSTRUAL -> R.drawable.ic_calendar
            NotificationType.FIRST_DAY -> R.drawable.ic_calendar
            NotificationType.OVULATION -> R.drawable.ic_calendar
            NotificationType.LAST_DAY -> R.drawable.ic_calendar
        }
    }

    private fun formatDate(date: Date): String {
        val formatter = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
        return formatter.format(date)
    }
}

