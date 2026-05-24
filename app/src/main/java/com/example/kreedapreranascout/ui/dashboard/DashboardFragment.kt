package com.example.kreedapreranascout.ui.dashboard

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.kreedapreranascout.R
import com.example.kreedapreranascout.data.local.AppDatabase
import com.example.kreedapreranascout.data.repository.StudentRepository
import com.example.kreedapreranascout.data.repository.UserRepository
import com.example.kreedapreranascout.databinding.FragmentDashboardBinding
import com.example.kreedapreranascout.ui.auth.AuthViewModel
import com.example.kreedapreranascout.util.SessionManager
import com.example.kreedapreranascout.util.ViewModelFactory

class DashboardFragment : Fragment(R.layout.fragment_dashboard) {
    private lateinit var binding: FragmentDashboardBinding
    private val viewModel: DashboardViewModel by viewModels {
        val db = AppDatabase.getDatabase(requireContext())
        ViewModelFactory(StudentRepository(db.studentDao(), db.performanceDao(), db.attendanceDao(), db.achievementDao()))
    }
    private val authViewModel: AuthViewModel by viewModels {
        val db = AppDatabase.getDatabase(requireContext())
        ViewModelFactory(UserRepository(db.teacherDao()))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDashboardBinding.bind(view)

        val sessionManager = SessionManager(requireContext())
        val teacherId = sessionManager.getTeacherId()

        authViewModel.getTeacherById(teacherId).observe(viewLifecycleOwner) { teacher ->
            teacher?.let {
                binding.welcomeTv.text = "Welcome, ${it.fullName}!"
                binding.subtitleTv.text = "Track. Analyze. Nurture Talent."
            }
        }

        viewModel.getStudentCount(teacherId).observe(viewLifecycleOwner) { count ->
            binding.studentCountTv.text = (count ?: 0).toString()
        }

        var topPerformerId: Long = -1
        viewModel.getTopPerformer("Sprint").observe(viewLifecycleOwner) { performances ->
            if (!performances.isNullOrEmpty()) {
                val top = performances.first()
                topPerformerId = top.student.id
                binding.topPerformerNameTv.text = top.student.fullName
                binding.topPerformerScoreTv.text = "${top.performance.testType} - ${top.performance.value} ${top.performance.unit}"
            } else {
                binding.topPerformerNameTv.text = "No data yet"
                binding.topPerformerScoreTv.text = "Log a trial to see results"
                topPerformerId = -1
            }
        }

        binding.summaryCard.setOnClickListener {
            if (topPerformerId != -1L) {
                val bundle = Bundle().apply { putLong("studentId", topPerformerId) }
                findNavController().navigate(R.id.action_dashboard_to_studentProfile, bundle)
            } else {
                Toast.makeText(context, "No athletes with trials found yet.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.studentListCard.setOnClickListener {
            findNavController().navigate(R.id.studentListFragment)
        }

        binding.addStudentCard.setOnClickListener {
            findNavController().navigate(R.id.addStudentFragment)
        }

        binding.leaderboardCard.setOnClickListener {
            findNavController().navigate(R.id.leaderboardFragment)
        }

        binding.attendanceCard.setOnClickListener {
            findNavController().navigate(R.id.attendanceFragment)
        }

        binding.batchTrialCard.setOnClickListener {
            findNavController().navigate(R.id.batchTrialFragment)
        }

        binding.talentCurveCard.setOnClickListener {
            Toast.makeText(context, "Select an athlete from the list to view their curve", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.studentListFragment)
        }
    }
}
