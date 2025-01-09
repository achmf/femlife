package com.example.femlife.data.menstrual.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MenstrualTrackerDao {

    @Query("SELECT * FROM menstrual_cycles WHERE userId = :userId ORDER BY startDate DESC")
    fun getAllMenstrualCycles(userId: String): Flow<List<MenstrualCycleEntity>>

    @Query("SELECT * FROM symptoms WHERE userId = :userId ORDER BY date DESC")
    fun getAllSymptoms(userId: String): Flow<List<SymptomEntity>>

    @Query("SELECT * FROM menstrual_cycles WHERE userId = :userId ORDER BY startDate DESC LIMIT 1")
    fun getLatestMenstrualCycle(userId: String): Flow<MenstrualCycleEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMenstrualCycle(menstrualCycleEntity: MenstrualCycleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSymptom(symptomEntity: SymptomEntity)
}
