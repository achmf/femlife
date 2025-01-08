package com.example.femlife.data.notification

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.util.Date

@Entity(tableName = "notifications")
@TypeConverters(DateConverter::class)
data class NotificationData(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val title: String,
    val message: String,
    val timestamp: Date,
    val type: NotificationType
)
