package com.example.kreedapreranascout.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.kreedapreranascout.data.repository.StudentRepository

class DashboardViewModel(private val repository: StudentRepository) : ViewModel() {
    fun getStudentCount(teacherId: Long) = repository.getStudentCount(teacherId).asLiveData()
    
    // In a real app, this would be a more complex query joining tables
    fun getTopPerformer(testType: String) = repository.getLeaderboard(testType).asLiveData()
}
