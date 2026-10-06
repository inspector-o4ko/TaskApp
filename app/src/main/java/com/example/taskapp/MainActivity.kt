package com.example.taskapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.taskapp.presentation.AppNavigation
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import androidx.compose.ui.unit.dp
import com.example.taskapp.ui.theme.TaskAppTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TaskAppTheme {
                AppNavigation()
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







