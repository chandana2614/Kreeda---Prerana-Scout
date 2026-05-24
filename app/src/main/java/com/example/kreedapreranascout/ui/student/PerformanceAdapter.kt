package com.example.kreedapreranascout.ui.student

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kreedapreranascout.data.model.Performance
import com.example.kreedapreranascout.databinding.ItemPerformanceBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PerformanceAdapter : ListAdapter<Performance, PerformanceAdapter.PerformanceViewHolder>(PerformanceDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PerformanceViewHolder {
        val binding = ItemPerformanceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PerformanceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PerformanceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PerformanceViewHolder(private val binding: ItemPerformanceBinding) : RecyclerView.ViewHolder(binding.root) {
        private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

        fun bind(performance: Performance) {
            binding.testTypeTv.text = performance.testType
            binding.dateTv.text = dateFormat.format(Date(performance.date))
            binding.valueTv.text = "${performance.value} ${performance.unit}"
        }
    }

    class PerformanceDiffCallback : DiffUtil.ItemCallback<Performance>() {
        override fun areItemsTheSame(oldItem: Performance, newItem: Performance): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Performance, newItem: Performance): Boolean = oldItem == newItem
    }
}
