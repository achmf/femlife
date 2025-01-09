package com.example.femlife.data.menstrual.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "menstrual_cycles")
data class MenstrualCycleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val startDate: Date,
    val endDate: Date,
    val cycleLength: Int,
    val periodLength: Int
)
