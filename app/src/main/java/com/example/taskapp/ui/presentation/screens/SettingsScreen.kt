package com.example.taskapp.ui.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.taskapp.domain.model.AppSettings
import com.example.taskapp.domain.model.AppTheme
import com.example.taskapp.domain.model.PriorityDisplay
import com.example.taskapp.domain.model.TaskCardSize
import com.example.taskapp.domain.model.TaskLayout
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ListItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settings: AppSettings,
    onThemeChange: (AppTheme) -> Unit,
    onTaskLayoutChange: (TaskLayout) -> Unit,
    onGridColumnsChange: (Int) -> Unit,
    onCardSizeChange: (TaskCardSize) -> Unit,
    onPriorityDisplayChange: (PriorityDisplay) -> Unit,
    onShowDescriptionChange: (Boolean) -> Unit,
    onResetSettings: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = { TopAppBar(title = { Text("Settings") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Text(
                text = "Внешний вид",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(
                    start = 16.dp,
                    top = 16.dp,
                    bottom = 8.dp
                )
            )

            ListItem(
                headlineContent = { Text("Как в системе") },
                supportingContent = {
                    Text("Использовать настройки Android")
                },
                trailingContent = {
                    RadioButton(
                        selected = settings.theme == AppTheme.SYSTEM,
                        onClick = { onThemeChange(AppTheme.SYSTEM) }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            ListItem(
                headlineContent = { Text("Светлая") },
                trailingContent = {
                    RadioButton(
                        selected = settings.theme == AppTheme.LIGHT,
                        onClick = { onThemeChange(AppTheme.LIGHT) }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            ListItem(
                headlineContent = { Text("Тёмная") },
                trailingContent = {
                    RadioButton(
                        selected = settings.theme == AppTheme.DARK,
                        onClick = { onThemeChange(AppTheme.DARK) }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            HorizontalDivider()
        }
    }
}