package com.example.kreedapreranascout.ui.performance

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kreedapreranascout.data.model.Student
import com.example.kreedapreranascout.databinding.ItemBatchTrialBinding

class BatchTrialAdapter : ListAdapter<Student, BatchTrialAdapter.BatchViewHolder>(StudentDiffCallback()) {

    private val valueMap = mutableMapOf<Long, Double>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BatchViewHolder {
        val binding = ItemBatchTrialBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BatchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BatchViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun getTrialData(): Map<Long, Double> = valueMap

    inner class BatchViewHolder(private val binding: ItemBatchTrialBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var currentTextWatcher: TextWatcher? = null

        fun bind(student: Student) {
            binding.studentNameTv.text = student.fullName
            
            // Remove previous watcher to avoid multiple triggers
            currentTextWatcher?.let { binding.valueEdit.removeTextChangedListener(it) }
            
            binding.valueEdit.setText(valueMap[student.id]?.toString() ?: "")

            currentTextWatcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    val value = s.toString().toDoubleOrNull()
                    if (value != null) {
                        valueMap[student.id] = value
                    } else {
                        valueMap.remove(student.id)
                    }
                }
            }
            binding.valueEdit.addTextChangedListener(currentTextWatcher)
        }
    }

    class StudentDiffCallback : DiffUtil.ItemCallback<Student>() {
        override fun areItemsTheSame(oldItem: Student, newItem: Student): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Student, newItem: Student): Boolean = oldItem == newItem
    }
}
