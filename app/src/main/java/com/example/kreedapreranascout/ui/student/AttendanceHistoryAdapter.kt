package com.example.kreedapreranascout.ui.student

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kreedapreranascout.R
import com.example.kreedapreranascout.data.model.Attendance
import com.example.kreedapreranascout.databinding.ItemAttendanceHistoryBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AttendanceHistoryAdapter : ListAdapter<Attendance, AttendanceHistoryAdapter.AttendanceViewHolder>(AttendanceDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendanceViewHolder {
        val binding = ItemAttendanceHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AttendanceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AttendanceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class AttendanceViewHolder(private val binding: ItemAttendanceHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

        fun bind(attendance: Attendance) {
            binding.dateTv.text = dateFormat.format(Date(attendance.date))
            binding.statusTv.text = attendance.status
            
            val colorRes = when (attendance.status) {
                "Present" -> android.R.color.holo_green_dark
                "Absent" -> android.R.color.holo_red_dark
                else -> android.R.color.darker_gray
            }
            binding.statusTv.setTextColor(ContextCompat.getColor(binding.root.context, colorRes))
        }
    }

    class AttendanceDiffCallback : DiffUtil.ItemCallback<Attendance>() {
        override fun areItemsTheSame(oldItem: Attendance, newItem: Attendance): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Attendance, newItem: Attendance): Boolean = oldItem == newItem
    }
}
