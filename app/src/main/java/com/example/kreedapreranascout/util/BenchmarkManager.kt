package com.example.kreedapreranascout.util

import com.example.kreedapreranascout.data.model.Achievement
import java.util.Locale

object BenchmarkManager {

    /**
     * Benchmarks for District Level Ready Badges.
     * Logic: If a student meets these criteria, a badge is automatically awarded.
     */
    private val benchmarks = mapOf(
        "Sprint" to 13.0, // seconds (less than or equal is better)
        "Jump" to 400.0,  // cm (greater than or equal is better)
        "Strength" to 50.0 // kg (greater than or equal is better)
    )

    /**
     * Checks if a performance value meets a milestone and returns an Achievement if it does.
     */
    fun checkMilestones(studentId: Long, testType: String, value: Double): Achievement? {
        // Handle case-insensitivity for test types
        val normalizedType = benchmarks.keys.find { it.equals(testType, ignoreCase = true) } ?: return null
        val benchmarkValue = benchmarks[normalizedType] ?: return null

        val isEligible = when (normalizedType) {
            "Sprint" -> value <= benchmarkValue // Faster time is better
            else -> value >= benchmarkValue      // More distance/weight is better
        }

        return if (isEligible) {
            Achievement(
                studentId = studentId,
                title = "District Level Ready: $normalizedType",
                level = "District",
                date = System.currentTimeMillis(),
                description = String.format(Locale.getDefault(), "Automatically awarded for achieving %.2f %s in %s.", 
                    value, getUnit(normalizedType), normalizedType)
            )
        } else {
            null
        }
    }

    private fun getUnit(testType: String): String {
        return when (testType) {
            "Sprint" -> "seconds"
            "Jump" -> "cm"
            "Strength" -> "kg"
            else -> "units"
        }
    }
}
