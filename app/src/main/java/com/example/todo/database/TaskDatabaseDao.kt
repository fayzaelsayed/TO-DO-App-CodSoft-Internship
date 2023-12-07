package com.example.todo.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TaskDatabaseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTask(task: TaskEntity)

    @Query("SELECT * FROM task_table")
    fun getAllTasks(): LiveData<List<TaskEntity>>

    @Query("SELECT * FROM task_table WHERE taskPriority ='HIGH'")
    fun getAllHighPriorityTasks(): LiveData<List<TaskEntity>>

    @Update
    fun updateTask(task: TaskEntity)

    @Query("UPDATE task_table SET taskType = :taskT WHERE id = :key ")
    fun updateTaskType(key: Int, taskT: String)

    @Query("UPDATE task_table SET taskPriority = :taskP WHERE id = :key ")
    fun updateTaskPriority(key: Int, taskP: String)

    @Delete
    fun deleteTask(task: TaskEntity)


}