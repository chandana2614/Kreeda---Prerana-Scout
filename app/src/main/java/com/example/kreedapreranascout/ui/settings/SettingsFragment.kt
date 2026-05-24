package com.example.kreedapreranascout.ui.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.kreedapreranascout.R
import com.example.kreedapreranascout.data.local.AppDatabase
import com.example.kreedapreranascout.data.repository.UserRepository
import com.example.kreedapreranascout.databinding.FragmentSettingsBinding
import com.example.kreedapreranascout.ui.auth.AuthViewModel
import com.example.kreedapreranascout.util.SessionManager
import com.example.kreedapreranascout.util.ViewModelFactory

class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private lateinit var binding: FragmentSettingsBinding
    private val viewModel: AuthViewModel by viewModels {
        val db = AppDatabase.getDatabase(requireContext())
        ViewModelFactory(UserRepository(db.teacherDao()))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSettingsBinding.bind(view)

        val sessionManager = SessionManager(requireContext())
        val teacherId = sessionManager.getTeacherId()

        // Optionally display teacher info if you add TextViews for it in the layout
        viewModel.getTeacherById(teacherId).observe(viewLifecycleOwner) { teacher ->
            teacher?.let {
                // For now, we can show it in the title or logs, 
                // but let's assume we might have a name/email field in future
            }
        }

        binding.logoutBtn.setOnClickListener {
            sessionManager.logout()
            // Clear backstack and go to login
            findNavController().navigate(R.id.action_settings_to_login) 
        }

        binding.switchAccountBtn.setOnClickListener {
            sessionManager.logout()
            findNavController().navigate(R.id.action_settings_to_login)
        }
    }
}
