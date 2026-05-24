package com.example.kreedapreranascout.ui.attendance

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kreedapreranascout.data.model.Student
import com.example.kreedapreranascout.databinding.ItemAttendanceBinding

class AttendanceAdapter : ListAdapter<Student, AttendanceAdapter.AttendanceViewHolder>(StudentDiffCallback()) {

    private val attendanceMap = mutableMapOf<Long, String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendanceViewHolder {
        val binding = ItemAttendanceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AttendanceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AttendanceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun getAttendanceData(): Map<Long, String> = attendanceMap

    inner class AttendanceViewHolder(private val binding: ItemAttendanceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(student: Student) {
            binding.studentNameTv.text = student.fullName
            
            // Set default to Present if not already set
            if (!attendanceMap.containsKey(student.id)) {
                attendanceMap[student.id] = "Present"
            }

            when (attendanceMap[student.id]) {
                "Present" -> binding.presentRb.isChecked = true
                "Absent" -> binding.absentRb.isChecked = true
            }

            binding.statusGroup.setOnCheckedChangeListener { _, checkedId ->
                val status = if (checkedId == binding.presentRb.id) "Present" else "Absent"
                attendanceMap[student.id] = status
            }
        }
    }

    class StudentDiffCallback : DiffUtil.ItemCallback<Student>() {
        override fun areItemsTheSame(oldItem: Student, newItem: Student): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Student, newItem: Student): Boolean = oldItem == newItem
    }
}
