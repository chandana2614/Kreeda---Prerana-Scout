package com.example.kreedapreranascout.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class PerformanceWithStudent(
    @Embedded val performance: Performance,
    @Relation(
        parentColumn = "studentId",
        entityColumn = "id"
    )
    val student: Student
)
