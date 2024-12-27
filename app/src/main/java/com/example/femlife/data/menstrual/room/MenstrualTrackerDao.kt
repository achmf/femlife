package com.example.femlife.data.menstrual.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MenstrualTrackerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMenstrualCycle(cycle: MenstrualCycleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSymptom(symptom: SymptomEntity)

    @Query("SELECT * FROM menstrual_cycles ORDER BY startDate DESC")
    fun getAllMenstrualCycles(): Flow<List<MenstrualCycleEntity>>

    @Query("SELECT * FROM symptoms ORDER BY date DESC")
    fun getAllSymptoms(): Flow<List<SymptomEntity>>

    @Query("SELECT * FROM menstrual_cycles ORDER BY startDate DESC LIMIT 1")
    fun getLatestMenstrualCycle(): Flow<MenstrualCycleEntity?>

    @Query("SELECT AVG(cycleLength) FROM menstrual_cycles")
    fun getAverageCycleLength(): Flow<Int?>
}

