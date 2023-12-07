package com.example.todo.ui.priority

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.database.TaskDatabaseDao
import com.example.todo.database.TaskEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PriorityViewModel @Inject constructor(private val taskDatabaseDao: TaskDatabaseDao) :
    ViewModel() {
    fun getAllHighPriorityTasksFromDatabase(): LiveData<List<TaskEntity>> {
        return taskDatabaseDao.getAllHighPriorityTasks()
    }

    fun updateTaskType(key: Int, taskT: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                taskDatabaseDao.updateTaskType(key, taskT)
            } catch (e: Exception) {
                Log.i("updateTaskType", "updateTaskType: $e")
            }
        }

    }

    fun updateTaskPriority(key: Int, taskP: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                taskDatabaseDao.updateTaskPriority(key, taskP)
            } catch (e: Exception) {
                Log.i("updateTaskPriority", "updateTaskPriority: $e")
            }
        }

    }

    fun deleteTaskFromDatabase(taskEntity: TaskEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                taskDatabaseDao.deleteTask(taskEntity)
            } catch (e: Exception) {
                Log.i("deleteTask", "deleteTask: $e")
            }
        }

    }
}