package com.example.kreedapreranascout.ui.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.kreedapreranascout.R
import com.example.kreedapreranascout.data.local.AppDatabase
import com.example.kreedapreranascout.data.repository.UserRepository
import com.example.kreedapreranascout.databinding.FragmentCoachProfileBinding
import com.example.kreedapreranascout.ui.auth.AuthViewModel
import com.example.kreedapreranascout.util.SessionManager
import com.example.kreedapreranascout.util.ViewModelFactory

class CoachProfileFragment : Fragment(R.layout.fragment_coach_profile) {
    private lateinit var binding: FragmentCoachProfileBinding
    private val viewModel: AuthViewModel by viewModels {
        val db = AppDatabase.getDatabase(requireContext())
        ViewModelFactory(UserRepository(db.teacherDao()))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCoachProfileBinding.bind(view)

        val sessionManager = SessionManager(requireContext())
        val teacherId = sessionManager.getTeacherId()

        viewModel.getTeacherById(teacherId).observe(viewLifecycleOwner) { teacher ->
            teacher?.let {
                binding.coachNameTv.text = it.fullName
                binding.coachEmailTv.text = it.email
                binding.schoolNameTv.text = it.schoolName
                binding.phoneTv.text = if (it.phone.isEmpty()) "Not provided" else it.phone
            }
        }
    }
}
