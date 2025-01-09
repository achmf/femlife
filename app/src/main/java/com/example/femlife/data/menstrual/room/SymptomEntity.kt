package com.example.femlife.data.menstrual.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.femlife.data.menstrual.SymptomType
import java.util.Date

@Entity(tableName = "symptoms")
data class SymptomEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val date: Date,
    val type: SymptomType,
    val severity: Int
)

