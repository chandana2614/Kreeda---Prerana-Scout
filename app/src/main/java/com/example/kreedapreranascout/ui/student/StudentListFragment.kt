package com.example.kreedapreranascout.ui.student

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kreedapreranascout.R
import com.example.kreedapreranascout.data.local.AppDatabase
import com.example.kreedapreranascout.data.model.Student
import com.example.kreedapreranascout.data.repository.StudentRepository
import com.example.kreedapreranascout.databinding.FragmentStudentListBinding
import com.example.kreedapreranascout.util.SessionManager
import com.example.kreedapreranascout.util.ViewModelFactory

class StudentListFragment : Fragment(R.layout.fragment_student_list) {
    private lateinit var binding: FragmentStudentListBinding
    private val viewModel: StudentViewModel by viewModels {
        val db = AppDatabase.getDatabase(requireContext())
        ViewModelFactory(StudentRepository(db.studentDao(), db.performanceDao(), db.attendanceDao(), db.achievementDao()))
    }
    private var allStudents = listOf<Student>()
    private lateinit var adapter: StudentAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentStudentListBinding.bind(view)

        val sessionManager = SessionManager(requireContext())
        val teacherId = sessionManager.getTeacherId()

        adapter = StudentAdapter { student ->
            val action = StudentListFragmentDirections.actionStudentListToStudentProfile(student.id)
            findNavController().navigate(action)
        }

        binding.studentRv.layoutManager = LinearLayoutManager(context)
        binding.studentRv.adapter = adapter

        viewModel.getAllStudents(teacherId).observe(viewLifecycleOwner) { students ->
            allStudents = students ?: listOf()
            updateList()
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                updateList()
                return true
            }
        })

        binding.sortChipGroup.setOnCheckedStateChangeListener { _, _ ->
            updateList()
        }
    }

    private fun updateList() {
        val query = binding.searchView.query.toString().lowercase()
        
        var filteredList = allStudents.filter {
            it.fullName.lowercase().contains(query) || it.rollNumber.lowercase().contains(query)
        }

        filteredList = when (binding.sortChipGroup.checkedChipId) {
            R.id.sortRollChip -> filteredList.sortedBy { it.rollNumber }
            R.id.sortBmiChip -> filteredList.sortedByDescending { it.bmi }
            else -> filteredList.sortedBy { it.fullName }
        }

        if (filteredList.isEmpty()) {
            binding.studentRv.visibility = View.GONE
            binding.emptyStateTv.visibility = View.VISIBLE
        } else {
            binding.studentRv.visibility = View.VISIBLE
            binding.emptyStateTv.visibility = View.GONE
            adapter.submitList(filteredList)
        }
    }
}
