package com.example.kreedapreranascout.ui.performance

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kreedapreranascout.R
import com.example.kreedapreranascout.data.local.AppDatabase
import com.example.kreedapreranascout.data.model.Performance
import com.example.kreedapreranascout.data.repository.StudentRepository
import com.example.kreedapreranascout.databinding.FragmentBatchTrialBinding
import com.example.kreedapreranascout.ui.student.StudentViewModel
import com.example.kreedapreranascout.util.BenchmarkManager
import com.example.kreedapreranascout.util.SessionManager
import com.example.kreedapreranascout.util.ViewModelFactory

class BatchTrialFragment : Fragment(R.layout.fragment_batch_trial) {
    private lateinit var binding: FragmentBatchTrialBinding
    private val viewModel: StudentViewModel by viewModels {
        val db = AppDatabase.getDatabase(requireContext())
        ViewModelFactory(StudentRepository(db.studentDao(), db.performanceDao(), db.attendanceDao(), db.achievementDao()))
    }
    private val adapter = BatchTrialAdapter()
    private val testTypes = arrayOf("Sprint", "Jump", "Strength", "Trial")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentBatchTrialBinding.bind(view)

        val sessionManager = SessionManager(requireContext())
        val teacherId = sessionManager.getTeacherId()

        setupSpinner()
        setupRecyclerView()

        viewModel.getAllStudents(teacherId).observe(viewLifecycleOwner) { students ->
            adapter.submitList(students)
        }

        binding.saveBatchBtn.setOnClickListener {
            saveAllTrials()
        }
    }

    private fun setupSpinner() {
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, testTypes)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.testTypeSpinner.adapter = spinnerAdapter
    }

    private fun setupRecyclerView() {
        binding.batchTrialRv.layoutManager = LinearLayoutManager(context)
        binding.batchTrialRv.adapter = adapter
    }

    private fun saveAllTrials() {
        val trialData = adapter.getTrialData()
        val testType = binding.testTypeSpinner.selectedItem.toString()
        val date = System.currentTimeMillis()
        
        val unit = when (testType) {
            "Sprint" -> "seconds"
            "Jump" -> "cm"
            "Strength" -> "kg"
            else -> "units"
        }

        if (trialData.isEmpty()) {
            Toast.makeText(context, "No values entered", Toast.LENGTH_SHORT).show()
            return
        }

        trialData.forEach { (studentId, value) ->
            val performance = Performance(
                studentId = studentId,
                testType = testType,
                value = value,
                unit = unit,
                attemptNumber = 1,
                date = date,
                remarks = "Batch Entry"
            )
            viewModel.addPerformance(performance)

            // Automatic Badge Logic
            val achievement = BenchmarkManager.checkMilestones(studentId, testType, value)
            if (achievement != null) {
                viewModel.addAchievement(achievement)
            }
        }

        Toast.makeText(context, "All trials saved. Badges awarded where applicable.", Toast.LENGTH_LONG).show()
        findNavController().popBackStack()
    }
}
