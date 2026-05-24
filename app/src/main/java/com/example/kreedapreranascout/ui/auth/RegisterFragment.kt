package com.example.kreedapreranascout.ui.auth

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.kreedapreranascout.R
import com.example.kreedapreranascout.data.local.AppDatabase
import com.example.kreedapreranascout.data.model.Teacher
import com.example.kreedapreranascout.data.repository.UserRepository
import com.example.kreedapreranascout.databinding.FragmentRegisterBinding
import com.example.kreedapreranascout.util.SessionManager
import com.example.kreedapreranascout.util.ViewModelFactory

class RegisterFragment : Fragment(R.layout.fragment_register) {
    private lateinit var binding: FragmentRegisterBinding
    private val viewModel: AuthViewModel by viewModels {
        val dao = AppDatabase.getDatabase(requireContext()).teacherDao()
        ViewModelFactory(UserRepository(dao))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRegisterBinding.bind(view)

        val sessionManager = SessionManager(requireContext())

        binding.registerBtn.setOnClickListener {
            if (validateInput()) {
                val name = binding.nameEdit.text.toString().trim()
                val email = binding.emailEdit.text.toString().trim()
                val school = binding.schoolEdit.text.toString().trim()
                val password = binding.passwordEdit.text.toString()

                val teacher = Teacher(
                    fullName = name,
                    email = email,
                    schoolName = school,
                    phone = "",
                    schoolAddress = "",
                    passwordHash = "" // Will be hashed in ViewModel
                )
                viewModel.register(teacher, password)
            }
        }

        binding.loginTv.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }

        viewModel.registrationStatus.observe(viewLifecycleOwner) { result ->
            result.onSuccess { teacherId ->
                sessionManager.saveSession(teacherId)
                findNavController().navigate(R.id.action_register_to_dashboard)
            }.onFailure {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
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

        val email = binding.emailEdit.text.toString().trim()
        if (email.isEmpty()) {
            binding.emailLayout.error = "Email is required"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailLayout.error = "Enter a valid email address"
            isValid = false
        } else {
            binding.emailLayout.error = null
        }

        val school = binding.schoolEdit.text.toString().trim()
        if (school.isEmpty()) {
            binding.schoolLayout.error = "School/College name is required"
            isValid = false
        } else {
            binding.schoolLayout.error = null
        }

        val password = binding.passwordEdit.text.toString()
        if (password.isEmpty()) {
            binding.passwordLayout.error = "Password is required"
            isValid = false
        } else if (password.length < 6) {
            binding.passwordLayout.error = "Password must be at least 6 characters"
            isValid = false
        } else {
            binding.passwordLayout.error = null
        }

        return isValid
    }
}
