package com.example.taskapp.ui.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskapp.domain.model.AppSettings
import com.example.taskapp.domain.model.AppTheme
import com.example.taskapp.domain.model.TaskLayout
import com.example.taskapp.domain.model.TaskCardSize
import com.example.taskapp.domain.model.PriorityDisplay
import com.example.taskapp.domain.model.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SettingsRepository
) : ViewModel() {

    val settings: StateFlow<AppSettings> =
        repository.settings.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AppSettings()
        )

    fun updateTheme(theme: AppTheme) {
        viewModelScope.launch {
            repository.updateTheme(theme)
        }
    }

    fun updateTaskLayout(layout: TaskLayout) {
        viewModelScope.launch {
            repository.updateTaskLayout(layout)
        }
    }

    fun updateGridColumns(columns: Int) {
        viewModelScope.launch {
            repository.updateGridColumns(columns)
        }
    }

    fun updateCardSize(size: TaskCardSize) {
        viewModelScope.launch {
            repository.updateCardSize(size)
        }
    }

    fun updatePriorityDisplay(display: PriorityDisplay) {
        viewModelScope.launch {
            repository.updatePriorityDisplay(display)
        }
    }

    fun updateShowDescription(show: Boolean) {
        viewModelScope.launch {
            repository.updateShowDescription(show)
        }
    }

    fun resetSettings() {
        viewModelScope.launch {
            repository.resetSettings()
        }
    }
}