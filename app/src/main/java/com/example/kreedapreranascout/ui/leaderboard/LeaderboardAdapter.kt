package com.example.kreedapreranascout.ui.leaderboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kreedapreranascout.data.model.PerformanceWithStudent
import com.example.kreedapreranascout.databinding.ItemLeaderboardBinding

class LeaderboardAdapter : ListAdapter<PerformanceWithStudent, LeaderboardAdapter.LeaderboardViewHolder>(PerformanceDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardViewHolder {
        val binding = ItemLeaderboardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LeaderboardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LeaderboardViewHolder, position: Int) {
        holder.bind(getItem(position), position + 1)
    }

    class LeaderboardViewHolder(private val binding: ItemLeaderboardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PerformanceWithStudent, rank: Int) {
            binding.rankTv.text = rank.toString()
            binding.studentNameTv.text = item.student.fullName
            binding.valueTv.text = String.format("%.2f %s", item.performance.value, item.performance.unit)
        }
    }

    class PerformanceDiffCallback : DiffUtil.ItemCallback<PerformanceWithStudent>() {
        override fun areItemsTheSame(oldItem: PerformanceWithStudent, newItem: PerformanceWithStudent): Boolean = 
            oldItem.performance.id == newItem.performance.id
        override fun areContentsTheSame(oldItem: PerformanceWithStudent, newItem: PerformanceWithStudent): Boolean = 
            oldItem == newItem
    }
}
