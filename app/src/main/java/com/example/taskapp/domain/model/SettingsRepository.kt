package com.example.taskapp.domain.model

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val settings: Flow<AppSettings>

    suspend fun updateTheme(theme: AppTheme)
    suspend fun updateTaskLayout(layout: TaskLayout)
    suspend fun updateGridColumns(columns: Int)
    suspend fun updateCardSize(size: TaskCardSize)
    suspend fun updatePriorityDisplay(display: PriorityDisplay)
    suspend fun updateShowDescription(show: Boolean)

    suspend fun resetSettings()
}