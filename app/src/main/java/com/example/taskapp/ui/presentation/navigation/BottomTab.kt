package com.example.taskapp.ui.presentation.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.taskapp.R
import com.example.taskapp.presentation.navigation.Routes

enum class BottomTab(
    val route: String,
    val icon: ImageVector,
    @StringRes val labelRes: Int
) {
    ACCOUNT(Routes.ACCOUNT, Icons.Default.AccountCircle, R.string.tab_account),
    CALENDAR(Routes.CALENDAR, Icons.Default.DateRange, R.string.tab_calendar),
    TASKS(Routes.TASKS, Icons.Default.CheckCircle, R.string.tab_tasks),
    TRACKER(Routes.TRACKER, Icons.Default.Star, R.string.tab_tracker),
    SETTINGS(Routes.SETTINGS, Icons.Default.Settings, R.string.tab_settings)
}