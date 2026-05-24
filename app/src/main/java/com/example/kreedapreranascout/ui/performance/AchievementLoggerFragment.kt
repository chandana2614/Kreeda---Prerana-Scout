package com.example.kreedapreranascout.ui.performance

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.kreedapreranascout.R
import com.example.kreedapreranascout.data.local.AppDatabase
import com.example.kreedapreranascout.data.model.Achievement
import com.example.kreedapreranascout.data.repository.StudentRepository
import com.example.kreedapreranascout.databinding.FragmentAchievementLoggerBinding
import com.example.kreedapreranascout.ui.student.StudentViewModel
import com.example.kreedapreranascout.util.ViewModelFactory

class AchievementLoggerFragment : Fragment(R.layout.fragment_achievement_logger) {
    private lateinit var binding: FragmentAchievementLoggerBinding
    private val args: AchievementLoggerFragmentArgs by navArgs()
    private val viewModel: StudentViewModel by viewModels {
        val db = AppDatabase.getDatabase(requireContext())
        ViewModelFactory(StudentRepository(db.studentDao(), db.performanceDao(), db.attendanceDao(), db.achievementDao()))
    }

    private val levels = arrayOf("School", "District", "State", "National", "International")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAchievementLoggerBinding.bind(view)

        setupSpinner()

        binding.saveBtn.setOnClickListener {
            saveAchievement()
        }

        viewModel.operationStatus.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(context, "Achievement saved successfully", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }.onFailure {
                Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupSpinner() {
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, levels)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.levelSpinner.adapter = spinnerAdapter
    }

    private fun saveAchievement() {
        val title = binding.titleEdit.text.toString().trim()
        val level = binding.levelSpinner.selectedItem.toString()
        val description = binding.descEdit.text.toString().trim()

        if (title.isEmpty()) {
            binding.titleLayout.error = "Title is required"
            return
        }
        binding.titleLayout.error = null

        val achievement = Achievement(
            studentId = args.studentId,
            title = title,
            level = level,
            date = System.currentTimeMillis(),
            description = if (description.isEmpty()) null else description
        )

        viewModel.addAchievement(achievement)
    }
}
