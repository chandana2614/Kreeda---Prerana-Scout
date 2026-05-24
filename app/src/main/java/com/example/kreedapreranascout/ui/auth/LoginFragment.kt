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
import com.example.kreedapreranascout.data.repository.UserRepository
import com.example.kreedapreranascout.databinding.FragmentLoginBinding
import com.example.kreedapreranascout.util.SessionManager
import com.example.kreedapreranascout.util.ViewModelFactory

class LoginFragment : Fragment(R.layout.fragment_login) {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel: AuthViewModel by viewModels {
        val dao = AppDatabase.getDatabase(requireContext()).teacherDao()
        ViewModelFactory(UserRepository(dao))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)

        val sessionManager = SessionManager(requireContext())

        binding.loginBtn.setOnClickListener {
            if (validateInput()) {
                val email = binding.emailEdit.text.toString().trim()
                val password = binding.passwordEdit.text.toString()
                viewModel.login(email, password)
            }
        }

        binding.registerTv.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_register)
        }

        viewModel.loginStatus.observe(viewLifecycleOwner) { result ->
            result.onSuccess { teacherId ->
                sessionManager.saveSession(teacherId)
                findNavController().navigate(R.id.action_login_to_dashboard)
            }.onFailure {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateInput(): Boolean {
        var isValid = true

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

        val password = binding.passwordEdit.text.toString()
        if (password.isEmpty()) {
            binding.passwordLayout.error = "Password is required"
            isValid = false
        } else {
            binding.passwordLayout.error = null
        }

        return isValid
    }
}
