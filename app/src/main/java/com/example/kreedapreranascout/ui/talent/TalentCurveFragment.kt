package com.example.kreedapreranascout.ui.talent

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.kreedapreranascout.R
import com.example.kreedapreranascout.data.local.AppDatabase
import com.example.kreedapreranascout.data.model.Performance
import com.example.kreedapreranascout.data.repository.StudentRepository
import com.example.kreedapreranascout.databinding.FragmentTalentCurveBinding
import com.example.kreedapreranascout.ui.student.StudentViewModel
import com.example.kreedapreranascout.util.ViewModelFactory
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TalentCurveFragment : Fragment(R.layout.fragment_talent_curve) {
    private var _binding: FragmentTalentCurveBinding? = null
    private val binding get() = _binding!!
    
    private val args: TalentCurveFragmentArgs by navArgs()
    private val viewModel: StudentViewModel by viewModels {
        val db = AppDatabase.getDatabase(requireContext())
        ViewModelFactory(StudentRepository(db.studentDao(), db.performanceDao(), db.attendanceDao(), db.achievementDao()))
    }

    private var allPerformances = listOf<Performance>()
    private var selectedRangeId = R.id.btnAll
    private var currentTestType: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTalentCurveBinding.bind(view)

        val studentId = args.studentId
        Log.d("TalentCurve", "Athlete ID: $studentId")
        
        if (studentId == -1L) {
            Toast.makeText(context, "Please select an athlete from the list first", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.getStudentById(studentId).observe(viewLifecycleOwner) { student ->
            student?.let {
                binding.nameTv.text = it.fullName
                binding.detailsTv.text = String.format(Locale.getDefault(), "Athlete ID: STU%d", 1000 + it.id)
            }
        }

        viewModel.getPerformance(studentId).observe(viewLifecycleOwner) { performances ->
            allPerformances = performances ?: emptyList()
            if (allPerformances.isNotEmpty()) {
                setupTestTypeSpinner(allPerformances)
            } else {
                showNoData(true)
            }
        }

        binding.timeRangeToggle.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                selectedRangeId = checkedId
                updateChart()
            }
        }
        binding.timeRangeToggle.check(R.id.btnAll)
    }

    private fun setupTestTypeSpinner(performances: List<Performance>) {
        val testTypes = performances.map { it.testType }.distinct().sorted()
        
        binding.testTypeSpinner.visibility = View.VISIBLE
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, testTypes)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.testTypeSpinner.adapter = spinnerAdapter

        binding.testTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                currentTestType = testTypes[position]
                updateChart()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun updateChart() {
        if (allPerformances.isEmpty() || currentTestType == null) return

        val calendar = Calendar.getInstance()
        val filteredByTime = when (selectedRangeId) {
            R.id.btn1M -> { calendar.add(Calendar.MONTH, -1); allPerformances.filter { it.date >= calendar.timeInMillis } }
            R.id.btn3M -> { calendar.add(Calendar.MONTH, -3); allPerformances.filter { it.date >= calendar.timeInMillis } }
            R.id.btn6M -> { calendar.add(Calendar.MONTH, -6); allPerformances.filter { it.date >= calendar.timeInMillis } }
            R.id.btn1Y -> { calendar.add(Calendar.YEAR, -1); allPerformances.filter { it.date >= calendar.timeInMillis } }
            else -> allPerformances
        }

        val filtered = filteredByTime.filter { it.testType == currentTestType }.sortedBy { it.date }

        if (filtered.isNotEmpty()) {
            showNoData(false)
            
            // Using a simple index-based approach for X axis to avoid precision issues
            val entries = filtered.mapIndexed { index, perf -> 
                Entry(index.toFloat(), perf.value.toFloat()) 
            }

            val dataSet = LineDataSet(entries, "$currentTestType Progress")
            styleDataSet(dataSet)

            binding.lineChart.data = LineData(dataSet)
            setupXAxis(filtered)
            binding.lineChart.animateX(600)
            binding.lineChart.invalidate()
            updateStats(filtered)
        } else {
            showNoData(true)
        }
    }

    private fun setupXAxis(data: List<Performance>) {
        val xAxis = binding.lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.valueFormatter = object : ValueFormatter() {
            private val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
            override fun getFormattedValue(value: Float): String {
                val index = value.toInt()
                return if (index >= 0 && index < data.size) {
                    dateFormat.format(Date(data[index].date))
                } else ""
            }
        }
        binding.lineChart.axisRight.isEnabled = false
        binding.lineChart.description.isEnabled = false
    }

    private fun showNoData(show: Boolean) {
        binding.noDataTv.visibility = if (show) View.VISIBLE else View.GONE
        binding.chartCard.visibility = if (show) View.INVISIBLE else View.VISIBLE
        binding.statsGrid.visibility = if (show) View.INVISIBLE else View.VISIBLE
    }

    private fun styleDataSet(dataSet: LineDataSet) {
        dataSet.color = resources.getColor(R.color.primary_purple, null)
        dataSet.setCircleColor(resources.getColor(R.color.primary_purple, null))
        dataSet.lineWidth = 3f
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        dataSet.setDrawFilled(true)
        dataSet.fillColor = resources.getColor(R.color.purple_200, null)
        dataSet.fillAlpha = 60
    }

    private fun updateStats(filtered: List<Performance>) {
        val testType = currentTestType?.lowercase() ?: ""
        val isLowerBetter = testType.contains("sprint") || testType.contains("time")
        
        val best = if (isLowerBetter) filtered.minByOrNull { it.value }?.value else filtered.maxByOrNull { it.value }?.value
        val improvement = if (isLowerBetter) filtered.first().value - filtered.last().value else filtered.last().value - filtered.first().value
        
        binding.bestTimeTv.text = String.format(Locale.getDefault(), "%.2f", best)
        binding.improvementTv.text = String.format(Locale.getDefault(), "%.2f", improvement)
        binding.improvementTv.setTextColor(if (improvement >= 0) 
            resources.getColor(android.R.color.holo_green_dark, null) else 
            resources.getColor(android.R.color.holo_red_dark, null))
        binding.totalTrialsTv.text = filtered.size.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
