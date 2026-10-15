package com.example.taskapp.ui.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.taskapp.domain.model.AppSettings
import com.example.taskapp.domain.model.AppTheme
import com.example.taskapp.domain.model.PriorityDisplay
import com.example.taskapp.domain.model.TaskLayout
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ListItem
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.taskapp.ui.presentation.components.LocalThemeReveal
import com.example.taskapp.ui.presentation.components.SettingsSection
import com.example.taskapp.ui.presentation.components.ThemeSwitch
import com.example.taskapp.R
import com.example.taskapp.ui.presentation.components.IconToggleSwitch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settings: AppSettings,
    onThemeChange: (AppTheme) -> Unit,
    onTaskLayoutChange: (TaskLayout) -> Unit,
    onPriorityDisplayChange: (PriorityDisplay) -> Unit,
    onShowDescriptionChange: (Boolean) -> Unit,
    onResetSettings: () -> Unit
) {
    val reveal = LocalThemeReveal.current
    val systemDark = isSystemInDarkTheme()

    val isDark = when (settings.theme) {
        AppTheme.SYSTEM -> systemDark
        AppTheme.LIGHT -> false
        AppTheme.DARK -> true
    }
    val followSystem = settings.theme == AppTheme.SYSTEM

    var followSwitchCenter by remember { mutableStateOf(Offset.Zero) }

    fun changeTheme(newTheme: AppTheme, origin: Offset) {
        val newDark = when (newTheme) {
            AppTheme.SYSTEM -> systemDark
            AppTheme.LIGHT -> false
            AppTheme.DARK -> true
        }
        if (reveal != null && newDark != isDark) {
            reveal.start(origin) { onThemeChange(newTheme) }
        } else {
            onThemeChange(newTheme)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = { TopAppBar(title = { Text(stringResource(R.string.tab_settings)) }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SettingsSection(title = stringResource(R.string.settings_thema)) {
                ThemeSwitch(
                    isDark = isDark,
                    onToggle = { dark, origin ->
                        changeTheme(if (dark) AppTheme.DARK else AppTheme.LIGHT, origin)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                )

                ListItem(
                    headlineContent = { Text(stringResource(R.string.settings_follow_system)) },
                    supportingContent = { Text(stringResource(R.string.settings_follow_system_desc)) },
                    trailingContent = {
                        Switch(
                            checked = followSystem,
                            onCheckedChange = { checked ->
                                val newTheme = when {
                                    checked -> AppTheme.SYSTEM
                                    systemDark -> AppTheme.DARK
                                    else -> AppTheme.LIGHT
                                }
                                changeTheme(newTheme, followSwitchCenter)
                            },
                            modifier = Modifier.onGloballyPositioned { coords ->
                                followSwitchCenter = coords.positionInRoot() +
                                        Offset(coords.size.width / 2f, coords.size.height / 2f)
                            }
                        )
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
            }

            SettingsSection(title = "Task layout"){
                IconToggleSwitch(
                    checked = settings.taskLayout == TaskLayout.GRID,
                    onCheckedChange = { isGrid, _ ->
                        onTaskLayoutChange(if (isGrid) TaskLayout.GRID else TaskLayout.LIST)
                    },
                    startIcon = painterResource(R.drawable.ic_list_view),
                    endIcon = painterResource(R.drawable.ic_grid_view),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                )
            }
        }
    }
}