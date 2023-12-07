package com.example.todo.ui.tasks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.R
import com.example.todo.database.TaskEntity
import com.example.todo.databinding.TaskItemBinding
import com.example.todo.utils.Action

class AllTasksAdapter :
    ListAdapter<TaskEntity, AllTasksAdapter.TaskViewHolder>(DiffCallback()) {
    private lateinit var buttonClickListener: OnButtonClickListener
    fun setOnButtonClickListener(listener: OnButtonClickListener) {
        buttonClickListener = listener
    }

    interface OnButtonClickListener {
        fun onButtonClick(action: Action, taskEntity: TaskEntity)
    }

    inner class TaskViewHolder(private val binding: TaskItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(taskEntity: TaskEntity) {
            binding.apply {
                when (taskEntity.taskType) {
                    "TODO" -> {
                        cbItemTask.isChecked = false
                        cvTaskItem.setBackgroundColor(cvTaskItem.context.getColor(R.color.lighter_red))
                    }
                    "DONE" -> {
                        cbItemTask.isChecked = true
                        cvTaskItem.setBackgroundColor(cvTaskItem.context.getColor(R.color.red_20))
                    }
                }
                cbItemTask.setOnClickListener {
                    buttonClickListener.onButtonClick(Action.TaskType, taskEntity)
                }
                cbItemTask.text = taskEntity.taskTitle
                tvItemTaskDescription.text = taskEntity.taskDescription
                tvItemDateTask.text = taskEntity.taskDate
                tvItemTimeTask.text = taskEntity.taskTime
                when (taskEntity.taskPriority) {
                    "REGULAR" -> {
                        ibItemHighPriority.setImageResource(R.drawable.star_outline)
                    }
                    "HIGH" -> {
                        ibItemHighPriority.setImageResource(R.drawable.star_filled)
                    }
                }
                ibItemHighPriority.setOnClickListener {
                    buttonClickListener.onButtonClick(Action.TaskPriority, taskEntity)
                }
                ibItemDelete.setOnClickListener {
                    buttonClickListener.onButtonClick(Action.TaskDelete, taskEntity)
                }
                ibItemEdit.setOnClickListener {
                    buttonClickListener.onButtonClick(Action.TaskEdit, taskEntity)
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<TaskEntity>() {
        override fun areItemsTheSame(oldItem: TaskEntity, newItem: TaskEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: TaskEntity,
            newItem: TaskEntity
        ): Boolean {
            return oldItem.toString() == newItem.toString()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding =
            TaskItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }


    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentItem = getItem(position)
        currentItem?.let {
            holder.bind(currentItem)
        }
    }


}