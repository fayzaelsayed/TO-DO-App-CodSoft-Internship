package com.example.todo.di

import android.app.Application
import androidx.room.Room
import com.example.todo.database.TaskDatabase
import com.example.todo.database.TaskDatabaseDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideRoomDatabaseBuilder(application: Application): TaskDatabase {
        return Room.databaseBuilder(
            application,
            TaskDatabase::class.java, "task_db"
        ).allowMainThreadQueries().build()
    }

    @Singleton
    @Provides
    fun provideUserDaoInstance(taskDatabase: TaskDatabase): TaskDatabaseDao {
        return taskDatabase.taskDatabaseDao
    }

}