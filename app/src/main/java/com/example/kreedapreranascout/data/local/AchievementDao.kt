package com.example.kreedapreranascout.data.local

import androidx.room.*
import com.example.kreedapreranascout.data.model.Achievement
import kotlinx.coroutines.flow.Flow

@Dao
interface AchievementDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievement(achievement: Achievement)

    @Query("SELECT * FROM achievements WHERE studentId = :studentId ORDER BY date DESC")
    fun getAchievementsForStudent(studentId: Long): Flow<List<Achievement>>
}
