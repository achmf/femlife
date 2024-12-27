package com.example.femlife.data.menstrual.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [MenstrualCycleEntity::class, SymptomEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class MenstrualTrackerDatabase : RoomDatabase() {
    abstract fun menstrualTrackerDao(): MenstrualTrackerDao

    companion object {
        @Volatile
        private var INSTANCE: MenstrualTrackerDatabase? = null

        fun getDatabase(context: Context): MenstrualTrackerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MenstrualTrackerDatabase::class.java,
                    "menstrual_tracker_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

