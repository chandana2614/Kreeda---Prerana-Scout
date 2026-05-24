package com.example.kreedapreranascout.data.repository

import com.example.kreedapreranascout.data.local.*
import com.example.kreedapreranascout.data.model.*
import kotlinx.coroutines.flow.Flow

class StudentRepository(
    private val studentDao: StudentDao,
    private val performanceDao: PerformanceDao,
    private val attendanceDao: AttendanceDao,
    private val achievementDao: AchievementDao
) {
    suspend fun addStudent(student: Student): Long = studentDao.insertStudent(student)
    suspend fun updateStudent(student: Student) = studentDao.updateStudent(student)
    suspend fun deleteStudent(student: Student) = studentDao.deleteStudent(student)
    fun getAllStudents(teacherId: Long): Flow<List<Student>> = studentDao.getAllStudents(teacherId)
    fun getStudentById(id: Long): Flow<Student?> = studentDao.getStudentById(id)
    fun getStudentCount(teacherId: Long): Flow<Int> = studentDao.getStudentCount(teacherId)
    fun searchStudents(teacherId: Long, query: String): Flow<List<Student>> = studentDao.searchStudents(teacherId, query)

    suspend fun addPerformance(performance: Performance) = performanceDao.insertPerformance(performance)
    fun getPerformanceForStudent(studentId: Long) = performanceDao.getPerformanceForStudent(studentId)
    fun getLeaderboard(testType: String): Flow<List<PerformanceWithStudent>> = performanceDao.getLeaderboardForTest(testType)

    suspend fun markAttendance(attendance: Attendance) = attendanceDao.insertAttendance(attendance)
    fun getAttendanceForStudent(studentId: Long) = attendanceDao.getAttendanceForStudent(studentId)

    suspend fun addAchievement(achievement: Achievement) = achievementDao.insertAchievement(achievement)
    fun getAchievementsForStudent(studentId: Long) = achievementDao.getAchievementsForStudent(studentId)
}
