package com.example.kreedapreranascout.ui.student

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.kreedapreranascout.R
import com.example.kreedapreranascout.data.local.AppDatabase
import com.example.kreedapreranascout.data.model.Student
import com.example.kreedapreranascout.data.repository.StudentRepository
import com.example.kreedapreranascout.databinding.FragmentAddStudentBinding
import com.example.kreedapreranascout.util.SessionManager
import com.example.kreedapreranascout.util.ViewModelFactory

class AddStudentFragment : Fragment(R.layout.fragment_add_student) {
    private lateinit var binding: FragmentAddStudentBinding
    private val args: AddStudentFragmentArgs by navArgs()
    private val viewModel: StudentViewModel by viewModels {
        val db = AppDatabase.getDatabase(requireContext())
        ViewModelFactory(StudentRepository(db.studentDao(), db.performanceDao(), db.attendanceDao(), db.achievementDao()))
    }

    private var isEditMode = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddStudentBinding.bind(view)

        val sessionManager = SessionManager(requireContext())
        val teacherId = sessionManager.getTeacherId()

        // CRITICAL: If teacherId is invalid, we cannot save students due to Foreign Key constraint
        if (teacherId == -1L) {
            Toast.makeText(context, "Session expired. Please log in again.", Toast.LENGTH_LONG).show()
            sessionManager.logout()
            findNavController().navigate(R.id.loginFragment)
            return
        }

        val studentId = args.studentId
        if (studentId != -1L) {
            isEditMode = true
            binding.title.text = "Edit Student"
            binding.saveBtn.text = "Update Student"
            loadStudentData(studentId)
        }

        binding.saveBtn.setOnClickListener {
            if (validateInput()) {
                val name = binding.nameEdit.text.toString().trim()
                val dob = binding.dobEdit.text.toString().trim()
                val age = binding.ageEdit.text.toString().toInt()
                val gender = binding.genderEdit.text.toString().trim()
                val email = binding.emailEdit.text.toString().trim()
                val height = binding.heightEdit.text.toString().toDouble()
                val weight = binding.weightEdit.text.toString().toDouble()
                val sport = binding.sportEdit.text.toString().trim()
                val rollNo = binding.rollNoEdit.text.toString().trim()
                val college = binding.collegeEdit.text.toString().trim()
                val classGrade = binding.classEdit.text.toString().trim()

                val bmi = weight / ((height / 100) * (height / 100))
                val student = Student(
                    id = if (isEditMode) studentId else 0,
                    fullName = name,
                    age = age,
                    dob = dob,
                    gender = gender,
                    classGrade = classGrade,
                    section = "A", 
                    rollNumber = rollNo,
                    usn = rollNo,
                    college = college,
                    primarySport = sport,
                    secondarySport = null,
                    height = height,
                    weight = weight,
                    bmi = bmi,
                    guardianName = "Not specified",
                    guardianContact = "0000000000",
                    email = if (email.isEmpty()) null else email,
                    address = "Not specified",
                    medicalNotes = null,
                    createdByTeacherId = teacherId
                )

                if (isEditMode) {
                    viewModel.updateStudent(student)
                } else {
                    viewModel.addStudent(student)
                }
            }
        }

        viewModel.addStudentStatus.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(context, "Student saved successfully", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }.onFailure {
                Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_LONG).show()
            }
        }

        viewModel.operationStatus.observe(viewLifecycleOwner) { result ->
            if (isEditMode) {
                result.onSuccess {
                    Toast.makeText(context, "Student updated successfully", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }.onFailure {
                    Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun loadStudentData(id: Long) {
        viewModel.getStudentById(id).observe(viewLifecycleOwner) { student ->
            student?.let {
                binding.nameEdit.setText(it.fullName)
                binding.dobEdit.setText(it.dob)
                binding.ageEdit.setText(it.age.toString())
                binding.genderEdit.setText(it.gender)
                binding.emailEdit.setText(it.email)
                binding.heightEdit.setText(it.height.toString())
                binding.weightEdit.setText(it.weight.toString())
                binding.sportEdit.setText(it.primarySport)
                binding.rollNoEdit.setText(it.rollNumber)
                binding.collegeEdit.setText(it.college)
                binding.classEdit.setText(it.classGrade)
            }
        }
    }

    private fun validateInput(): Boolean {
        var isValid = true

        val name = binding.nameEdit.text.toString().trim()
        if (name.isEmpty()) {
            binding.nameLayout.error = "Name is required"
            isValid = false
        } else {
            binding.nameLayout.error = null
        }

        val ageStr = binding.ageEdit.text.toString().trim()
        val age = ageStr.toIntOrNull()
        if (age == null || age !in 5..100) {
            binding.ageLayout.error = "Enter age between 5 and 100"
            isValid = false
        } else {
            binding.ageLayout.error = null
        }

        val gender = binding.genderEdit.text.toString().trim()
        if (gender.isEmpty()) {
            binding.genderLayout.error = "Gender is required"
            isValid = false
        } else {
            binding.genderLayout.error = null
        }

        val heightStr = binding.heightEdit.text.toString().trim()
        val height = heightStr.toDoubleOrNull()
        if (height == null || height !in 50.0..300.0) {
            binding.heightLayout.error = "Enter valid height (50-300 cm)"
            isValid = false
        } else {
            binding.heightLayout.error = null
        }

        val weightStr = binding.weightEdit.text.toString().trim()
        val weight = weightStr.toDoubleOrNull()
        if (weight == null || weight !in 10.0..500.0) {
            binding.weightLayout.error = "Enter valid weight (10-500 kg)"
            isValid = false
        } else {
            binding.weightLayout.error = null
        }

        val sport = binding.sportEdit.text.toString().trim()
        if (sport.isEmpty()) {
            binding.sportLayout.error = "Primary sport is required"
            isValid = false
        } else {
            binding.sportLayout.error = null
        }

        val rollNo = binding.rollNoEdit.text.toString().trim()
        if (rollNo.isEmpty()) {
            binding.rollNoLayout.error = "Roll number is required"
            isValid = false
        } else {
            binding.rollNoLayout.error = null
        }

        if (!isValid) {
            Toast.makeText(context, "Please correct the errors in the form", Toast.LENGTH_SHORT).show()
        }

        return isValid
    }
}
