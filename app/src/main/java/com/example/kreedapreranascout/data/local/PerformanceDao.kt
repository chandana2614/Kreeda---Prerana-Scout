package com.example.kreedapreranascout.data.local

import androidx.room.*
import com.example.kreedapreranascout.data.model.Performance
import com.example.kreedapreranascout.data.model.PerformanceWithStudent
import kotlinx.coroutines.flow.Flow

@Dao
interface PerformanceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPerformance(performance: Performance): Long

    @Query("SELECT * FROM performance_records WHERE studentId = :studentId ORDER BY date DESC")
    fun getPerformanceForStudent(studentId: Long): Flow<List<Performance>>

    @Transaction
    @Query("SELECT * FROM performance_records WHERE testType = :testType ORDER BY value DESC")
    fun getLeaderboardForTest(testType: String): Flow<List<PerformanceWithStudent>>
}
