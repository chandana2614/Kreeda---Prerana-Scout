package com.example.kreedapreranascout.ui.student

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.kreedapreranascout.data.model.Achievement
import com.example.kreedapreranascout.data.model.Attendance
import com.example.kreedapreranascout.data.model.Performance
import com.example.kreedapreranascout.data.model.Student
import com.example.kreedapreranascout.data.repository.StudentRepository
import kotlinx.coroutines.launch

class StudentViewModel(private val repository: StudentRepository) : ViewModel() {
    private val _addStudentStatus = MutableLiveData<Result<Long>>()
    val addStudentStatus: LiveData<Result<Long>> = _addStudentStatus

    private val _operationStatus = MutableLiveData<Result<Unit>>()
    val operationStatus: LiveData<Result<Unit>> = _operationStatus

    fun addStudent(student: Student) {
        viewModelScope.launch {
            try {
                val id = repository.addStudent(student)
                _addStudentStatus.value = Result.success(id)
            } catch (e: Exception) {
                _addStudentStatus.value = Result.failure(e)
            }
        }
    }

    fun updateStudent(student: Student) {
        viewModelScope.launch {
            try {
                repository.updateStudent(student)
                _operationStatus.value = Result.success(Unit)
            } catch (e: Exception) {
                _operationStatus.value = Result.failure(e)
            }
        }
    }

    fun deleteStudent(student: Student) {
        viewModelScope.launch {
            try {
                repository.deleteStudent(student)
                _operationStatus.value = Result.success(Unit)
            } catch (e: Exception) {
                _operationStatus.value = Result.failure(e)
            }
        }
    }

    fun addPerformance(performance: Performance) {
        viewModelScope.launch {
            try {
                repository.addPerformance(performance)
                _operationStatus.value = Result.success(Unit)
            } catch (e: Exception) {
                _operationStatus.value = Result.failure(e)
            }
        }
    }

    fun addAchievement(achievement: Achievement) {
        viewModelScope.launch {
            try {
                repository.addAchievement(achievement)
                _operationStatus.value = Result.success(Unit)
            } catch (e: Exception) {
                _operationStatus.value = Result.failure(e)
            }
        }
    }

    fun markAttendance(attendance: Attendance) {
        viewModelScope.launch {
            try {
                repository.markAttendance(attendance)
                _operationStatus.value = Result.success(Unit)
            } catch (e: Exception) {
                _operationStatus.value = Result.failure(e)
            }
        }
    }

    fun getAllStudents(teacherId: Long) = repository.getAllStudents(teacherId).asLiveData()
    fun getStudentById(id: Long) = repository.getStudentById(id).asLiveData()
    fun searchStudents(teacherId: Long, query: String) = repository.searchStudents(teacherId, query).asLiveData()
    
    fun getPerformance(studentId: Long) = repository.getPerformanceForStudent(studentId).asLiveData()
    fun getAchievements(studentId: Long) = repository.getAchievementsForStudent(studentId).asLiveData()
    fun getAttendance(studentId: Long) = repository.getAttendanceForStudent(studentId).asLiveData()

    fun getLeaderboard(testType: String) = repository.getLeaderboard(testType).asLiveData()
}
