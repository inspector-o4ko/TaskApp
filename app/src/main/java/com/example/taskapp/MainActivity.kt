package com.example.taskapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.taskapp.presentation.AppNavigation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppNavigation()

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







