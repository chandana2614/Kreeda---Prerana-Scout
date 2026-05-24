package com.example.kreedapreranascout.ui.student

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kreedapreranascout.R
import com.example.kreedapreranascout.data.local.AppDatabase
import com.example.kreedapreranascout.data.model.Student
import com.example.kreedapreranascout.data.repository.StudentRepository
import com.example.kreedapreranascout.databinding.FragmentStudentProfileBinding
import com.example.kreedapreranascout.ui.performance.AchievementAdapter
import com.example.kreedapreranascout.util.ViewModelFactory
import java.util.Locale

class StudentProfileFragment : Fragment(R.layout.fragment_student_profile) {
    private lateinit var binding: FragmentStudentProfileBinding
    private val args: StudentProfileFragmentArgs by navArgs()
    private val viewModel: StudentViewModel by viewModels {
        val db = AppDatabase.getDatabase(requireContext())
        ViewModelFactory(StudentRepository(db.studentDao(), db.performanceDao(), db.attendanceDao(), db.achievementDao()))
    }
    private val achievementAdapter = AchievementAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentStudentProfileBinding.bind(view)

        val studentId = args.studentId

        viewModel.getStudentById(studentId).observe(viewLifecycleOwner) { student ->
            if (student != null) {
                bindStudentData(student)
            } else {
                Toast.makeText(context, "Athlete not found", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }

        viewModel.getAchievements(studentId).observe(viewLifecycleOwner) { achievements ->
            achievementAdapter.submitList(achievements)
        }

        viewModel.getPerformance(studentId).observe(viewLifecycleOwner) { performances ->
            if (performances != null) {
                binding.totalTrialsTv.text = performances.size.toString()
                
                val sprintTrials = performances.filter { it.testType.equals("Sprint", true) }
                if (sprintTrials.isNotEmpty()) {
                    val bestSprint = sprintTrials.minByOrNull { it.value }
                    binding.best100mTv.text = String.format(Locale.getDefault(), "%.2f s", bestSprint?.value)
                } else {
                    binding.best100mTv.text = "--"
                }

                val jumpTrials = performances.filter { it.testType.equals("Jump", true) }
                if (jumpTrials.isNotEmpty()) {
                    val bestJump = jumpTrials.maxByOrNull { it.value }
                    binding.bestJumpTv.text = String.format(Locale.getDefault(), "%.2f m", (bestJump?.value ?: 0.0) / 100.0)
                } else {
                    binding.bestJumpTv.text = "--"
                }
            }
        }

        binding.achievementsRv.layoutManager = LinearLayoutManager(context)
        binding.achievementsRv.adapter = achievementAdapter

        binding.editBtn.setOnClickListener {
            val action = StudentProfileFragmentDirections.actionStudentProfileToEditStudent(studentId)
            findNavController().navigate(action)
        }

        binding.addTrialFab.setOnClickListener {
            val action = StudentProfileFragmentDirections.actionStudentProfileToTrialLogger(studentId)
            findNavController().navigate(action)
        }

        binding.viewCurveBtn.setOnClickListener {
            try {
                val action = StudentProfileFragmentDirections.actionStudentProfileToTalentCurve(studentId)
                findNavController().navigate(action)
            } catch (e: Exception) {
                Toast.makeText(context, "Unable to open curve: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun bindStudentData(student: Student) {
        binding.nameTv.text = student.fullName
        binding.studentIdTv.text = String.format(Locale.getDefault(), "ID: STU%d", 1000 + student.id)
        binding.classDobTv.text = String.format(Locale.getDefault(), "Class: %s | DOB: %s", student.classGrade, student.dob ?: "N/A")
        binding.genderTv.text = String.format(Locale.getDefault(), "Gender: %s", student.gender)
        
        binding.phoneTv.text = student.guardianContact
        binding.emailTv.text = student.email ?: "--"
        binding.addressTv.text = student.address
        binding.joinedTv.text = "10 Jan 2024"
    }
}
