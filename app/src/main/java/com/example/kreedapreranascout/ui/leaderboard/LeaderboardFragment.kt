package com.example.kreedapreranascout.ui.leaderboard

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kreedapreranascout.R
import com.example.kreedapreranascout.data.local.AppDatabase
import com.example.kreedapreranascout.data.model.PerformanceWithStudent
import com.example.kreedapreranascout.data.repository.StudentRepository
import com.example.kreedapreranascout.databinding.FragmentLeaderboardBinding
import com.example.kreedapreranascout.ui.student.StudentViewModel
import com.example.kreedapreranascout.util.ViewModelFactory

class LeaderboardFragment : Fragment(R.layout.fragment_leaderboard) {
    private lateinit var binding: FragmentLeaderboardBinding
    private val viewModel: StudentViewModel by viewModels {
        val db = AppDatabase.getDatabase(requireContext())
        ViewModelFactory(StudentRepository(db.studentDao(), db.performanceDao(), db.attendanceDao(), db.achievementDao()))
    }
    private val adapter = LeaderboardAdapter()
    private var fullList = listOf<PerformanceWithStudent>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLeaderboardBinding.bind(view)

        setupRecyclerView()
        setupSpinner()
        setupSearchView()
    }

    private fun setupRecyclerView() {
        binding.leaderboardRv.layoutManager = LinearLayoutManager(context)
        binding.leaderboardRv.adapter = adapter
    }

    private fun setupSpinner() {
        val testTypes = arrayOf("Sprint", "Jump", "Strength", "Trial")
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, testTypes)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.testTypeSpinner.adapter = spinnerAdapter

        binding.testTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedType = testTypes[position]
                viewModel.getLeaderboard(selectedType).observe(viewLifecycleOwner) { list ->
                    fullList = list ?: listOf()
                    filterList(binding.searchView.query.toString())
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterList(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }
        })
    }

    private fun filterList(query: String?) {
        val filteredList = if (query.isNullOrBlank()) {
            fullList
        } else {
            fullList.filter { 
                it.student.fullName.contains(query, ignoreCase = true) || 
                it.student.rollNumber.contains(query, ignoreCase = true)
            }
        }

        if (filteredList.isEmpty()) {
            binding.leaderboardRv.visibility = View.GONE
            binding.emptyStateTv.visibility = View.VISIBLE
        } else {
            binding.leaderboardRv.visibility = View.VISIBLE
            binding.emptyStateTv.visibility = View.GONE
            adapter.submitList(filteredList)
        }
    }
}
