package com.example.taskapp.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.taskapp.domain.model.AppSettings
import com.example.taskapp.domain.model.AppTheme
import com.example.taskapp.domain.model.TaskLayout
import com.example.taskapp.domain.model.PriorityDisplay
import com.example.taskapp.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    private object Keys {
        val theme = stringPreferencesKey("theme")
        val taskLayout = stringPreferencesKey("task_layout")
        val priorityDisplay = stringPreferencesKey("priority_display")
        val showDescription = booleanPreferencesKey("show_description")
    }

    override val settings: Flow<AppSettings> =

        dataStore.data
            .catch { e ->
                if (e is IOException) emit(emptyPreferences()) else throw e
            }
            .map { preferences ->
            AppSettings(
                theme = preferences[Keys.theme]
                    ?.let { value ->
                        AppTheme.entries.find { it.name == value }
                    } ?: AppTheme.SYSTEM,

                taskLayout = preferences[Keys.taskLayout]
                    ?.let { value ->
                        TaskLayout.entries.find { it.name == value }
                    } ?: TaskLayout.LIST,

                priorityDisplay = preferences[Keys.priorityDisplay]
                    ?.let { value ->
                        PriorityDisplay.entries.find { it.name == value }
                    } ?: PriorityDisplay.COLOR_STRIP,

                showDescription = preferences[Keys.showDescription] ?: true
            )
        }

    override suspend fun updateTheme(theme: AppTheme) {
        dataStore.edit { it[Keys.theme] = theme.name }
    }

    override suspend fun updateTaskLayout(layout: TaskLayout) {
        dataStore.edit { it[Keys.taskLayout] = layout.name }
    }

    override suspend fun updatePriorityDisplay(display: PriorityDisplay) {
        dataStore.edit { it[Keys.priorityDisplay] = display.name }
    }

    override suspend fun updateShowDescription(show: Boolean) {
        dataStore.edit { it[Keys.showDescription] = show }
    }

    override suspend fun resetSettings() {
        dataStore.edit { it.clear() }
    }
}