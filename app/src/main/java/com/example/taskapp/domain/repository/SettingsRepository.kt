package com.example.taskapp.domain.repository

import com.example.taskapp.domain.model.AppSettings
import com.example.taskapp.domain.model.AppTheme
import com.example.taskapp.domain.model.PriorityDisplay
import com.example.taskapp.domain.model.TaskLayout
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val settings: Flow<AppSettings>

    suspend fun updateTheme(theme: AppTheme)
    suspend fun updateTaskLayout(layout: TaskLayout)
    suspend fun updatePriorityDisplay(display: PriorityDisplay)
    suspend fun updateShowDescription(show: Boolean)

    suspend fun resetSettings()
}