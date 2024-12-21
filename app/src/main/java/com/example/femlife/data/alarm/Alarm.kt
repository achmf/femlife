package com.example.femlife.data.alarm

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarms")
data class Alarm(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,
    val time: String,
    val doctorName: String?,
    val location: String,
    val notes: String,
    val oneDayBefore: Boolean,
    val dayOf: Boolean,
    val soundEnabled: Boolean
)
