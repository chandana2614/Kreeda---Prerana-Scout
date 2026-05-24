package com.example.kreedapreranascout.ui.performance

import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.kreedapreranascout.R
import com.example.kreedapreranascout.data.local.AppDatabase
import com.example.kreedapreranascout.data.model.Performance
import com.example.kreedapreranascout.data.repository.StudentRepository
import com.example.kreedapreranascout.databinding.FragmentTrialLoggerBinding
import com.example.kreedapreranascout.ui.student.StudentViewModel
import com.example.kreedapreranascout.util.BenchmarkManager
import com.example.kreedapreranascout.util.ViewModelFactory
import java.util.Timer
import java.util.TimerTask

class TrialLoggerFragment : Fragment(R.layout.fragment_trial_logger) {
    private lateinit var binding: FragmentTrialLoggerBinding
    private val args: TrialLoggerFragmentArgs by navArgs()
    private val viewModel: StudentViewModel by viewModels {
        val db = AppDatabase.getDatabase(requireContext())
        ViewModelFactory(StudentRepository(db.studentDao(), db.performanceDao(), db.attendanceDao(), db.achievementDao()))
    }

    private var timer: Timer? = null
    private var startTime = 0L
    private var isRunning = false
    private val testTypes = arrayOf("Sprint", "Jump", "Strength", "Trial")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTrialLoggerBinding.bind(view)

        setupSpinner()

        binding.startStopBtn.setOnClickListener {
            if (isRunning) stopTimer() else startTimer()
        }

        binding.resetBtn.setOnClickListener {
            resetTimer()
        }

        binding.saveTrialBtn.setOnClickListener {
            saveTrial()
        }

        viewModel.operationStatus.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(context, "Performance saved", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }.onFailure {
                Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupSpinner() {
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, testTypes)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.testTypeSpinner.adapter = spinnerAdapter
    }

    private fun startTimer() {
        startTime = SystemClock.elapsedRealtime()
        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                val elapsed = SystemClock.elapsedRealtime() - startTime
                activity?.runOnUiThread {
                    binding.timerTv.text = formatTime(elapsed)
                }
            }
        }, 0, 10)
        isRunning = true
        binding.startStopBtn.text = "Stop"
    }

    private fun stopTimer() {
        timer?.cancel()
        isRunning = false
        binding.startStopBtn.text = "Start"
    }

    private fun resetTimer() {
        stopTimer()
        binding.timerTv.text = "00:00:00"
    }

    private fun formatTime(millis: Long): String {
        val minutes = (millis / 60000)
        val seconds = (millis % 60000) / 1000
        val hundredths = (millis % 1000) / 10
        return String.format("%02d:%02d:%02d", minutes, seconds, hundredths)
    }

    private fun saveTrial() {
        val studentId = args.studentId
        val testType = binding.testTypeSpinner.selectedItem.toString()
        val valueStr = binding.valueEdit.text.toString()
        
        val unit = when (testType) {
            "Sprint" -> "seconds"
            "Jump" -> "cm"
            "Strength" -> "kg"
            else -> "units"
        }

        val timeValue = if (binding.timerTv.text != "00:00:00") {
             val elapsed = SystemClock.elapsedRealtime() - startTime
             elapsed.toDouble() / 1000.0
        } else {
            valueStr.toDoubleOrNull() ?: 0.0
        }

        if (timeValue > 0) {
            val performance = Performance(
                studentId = studentId,
                testType = testType, 
                value = timeValue,
                unit = unit,
                attemptNumber = 1,
                date = System.currentTimeMillis(),
                remarks = ""
            )
            viewModel.addPerformance(performance)
            
            // Check for milestones and award badges automatically
            val achievement = BenchmarkManager.checkMilestones(studentId, testType, timeValue)
            if (achievement != null) {
                viewModel.addAchievement(achievement)
                Toast.makeText(context, "New Badge Awarded: ${achievement.title}!", Toast.LENGTH_LONG).show()
            }

        } else {
            Toast.makeText(context, "Enter a valid value or use timer", Toast.LENGTH_SHORT).show()
        }
    }
}
