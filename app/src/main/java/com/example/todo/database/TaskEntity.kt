package com.example.todo.database


import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName ="task_table")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    var taskTitle: String,
    var taskDescription: String,
    var taskDate: String,
    var taskTime: String,
    val taskType: String,
    val taskPriority: String
):Parcelable