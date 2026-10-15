package com.example.taskapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.*
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.taskapp.presentation.AppNavigation
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.taskapp.domain.model.AppTheme
import com.example.taskapp.ui.presentation.components.ThemeRevealHost
import com.example.taskapp.ui.presentation.settings.SettingsViewModel
import com.example.taskapp.ui.theme.TaskAppTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        splashScreen.setKeepOnScreenCondition {
            settingsViewModel.settings.value == null
        }

        setContent {
            val settings by settingsViewModel.settings.collectAsStateWithLifecycle()

            val darkTheme = when (settings?.theme ?: AppTheme.SYSTEM) {
                AppTheme.SYSTEM -> isSystemInDarkTheme()
                AppTheme.LIGHT -> false
                AppTheme.DARK -> true
            }

            TaskAppTheme(darkTheme = darkTheme) {
                ThemeRevealHost {
                    AppNavigation(settingsViewModel = settingsViewModel)
                }
            }
        }
    }
}




/*


          ┌──────────────────┐
          │ Архитектура      │
          │ + Hilt + Room    │
          └────────┬─────────┘
                   ↓
             SavedStateHandle
                   ↓
          EditTaskViewModel
                   ↓
             UI/UX Compose <---
                   ↓
                Tests
                   ↓
          ┌──────────────────┐
          │ Retrofit         │
          │ REST API         │
          └────────┬─────────┘
                   ↓
          Repository:
          Local + Remote
                   ↓
             Offline-first
                   ↓
             Paging 3
                   ↓
             WorkManager
                   ↓
        полноценное Android-приложение


 */







