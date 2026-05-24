package com.example.kreedapreranascout.ui.performance

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kreedapreranascout.data.model.Achievement
import com.example.kreedapreranascout.databinding.ItemAchievementBinding

class AchievementAdapter : ListAdapter<Achievement, AchievementAdapter.AchievementViewHolder>(AchievementDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementViewHolder {
        val binding = ItemAchievementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AchievementViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AchievementViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class AchievementViewHolder(private val binding: ItemAchievementBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(achievement: Achievement) {
            binding.titleTv.text = achievement.title
            binding.descTv.text = achievement.description ?: "${achievement.level} Achievement"
            
            // Set medal color based on level
            val colorStr = when(achievement.level) {
                "National", "International" -> "#FFD700" // Gold
                "State" -> "#C0C0C0" // Silver
                "District" -> "#CD7F32" // Bronze
                else -> "#FFB300"
            }
            binding.medalIcon.setColorFilter(Color.parseColor(colorStr))
        }
    }

    class AchievementDiffCallback : DiffUtil.ItemCallback<Achievement>() {
        override fun areItemsTheSame(oldItem: Achievement, newItem: Achievement): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Achievement, newItem: Achievement): Boolean = oldItem == newItem
    }
}
