package com.example.todo.ui.edittask

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.database.TaskDatabaseDao
import com.example.todo.database.TaskEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditTaskViewModel @Inject constructor(
    private val taskDatabaseDao: TaskDatabaseDao
) : ViewModel() {
    fun updateTaskInDatabase(taskEntity: TaskEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                taskDatabaseDao.updateTask(taskEntity)
            } catch (e: Exception) {
                Log.i("updateTaskInDatabase", "updateTaskInDatabase: $e ")
            }
        }
    }

}