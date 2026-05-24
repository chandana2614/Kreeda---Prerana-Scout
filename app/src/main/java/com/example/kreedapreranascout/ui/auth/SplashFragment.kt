package com.example.kreedapreranascout.ui.auth

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.kreedapreranascout.R
import com.example.kreedapreranascout.data.local.AppDatabase
import com.example.kreedapreranascout.data.repository.UserRepository
import com.example.kreedapreranascout.util.SessionManager
import com.example.kreedapreranascout.util.ViewModelFactory

class SplashFragment : Fragment(R.layout.fragment_splash) {
    
    private val viewModel: AuthViewModel by viewModels {
        val dao = AppDatabase.getDatabase(requireContext()).teacherDao()
        ViewModelFactory(UserRepository(dao))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val sessionManager = SessionManager(requireContext())
        
        Handler(Looper.getMainLooper()).postDelayed({
            if (sessionManager.isLoggedIn()) {
                val teacherId = sessionManager.getTeacherId()
                // Verify teacher still exists in DB (important after migrations)
                viewModel.getTeacherById(teacherId).observe(viewLifecycleOwner) { teacher ->
                    if (teacher != null) {
                        findNavController().navigate(R.id.action_splash_to_dashboard)
                    } else {
                        sessionManager.logout()
                        findNavController().navigate(R.id.action_splash_to_login)
                    }
                }
            } else {
                findNavController().navigate(R.id.action_splash_to_login)
            }
        }, 2000)
    }
}
