package com.example.taskapp.di

import android.content.Context
import androidx.room.Room
import com.example.taskapp.data.local.MIGRATION_1_2
import com.example.taskapp.data.local.MIGRATION_2_3
import com.example.taskapp.data.local.MIGRATION_3_4
import com.example.taskapp.data.local.TaskDao
import com.example.taskapp.data.local.TaskDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): TaskDatabase {
        return Room.databaseBuilder(
            context,
            TaskDatabase::class.java,
            "task_database"
        )
            .addMigrations(
                MIGRATION_1_2,
                MIGRATION_2_3,
                MIGRATION_3_4
            )
            .build()
    }

    @Provides
    fun provideTaskDao(
        database: TaskDatabase
    ): TaskDao {
        return database.taskDao()
    }
}