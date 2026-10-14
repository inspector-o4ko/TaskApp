package com.example.taskapp.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.taskapp.presentation.screens.AddTaskScreen
import com.example.taskapp.presentation.screens.EditTaskScreen
import com.example.taskapp.presentation.navigation.Routes
import com.example.taskapp.presentation.screens.TaskListScreen
import com.example.taskapp.presentation.task.EditTaskViewModel
import com.example.taskapp.presentation.task.TaskUiState
import com.example.taskapp.presentation.task.TaskViewModel
import com.example.taskapp.ui.presentation.navigation.BottomTab
import com.example.taskapp.ui.presentation.screens.AccountScreen
import com.example.taskapp.ui.presentation.screens.CalendarScreen
import com.example.taskapp.ui.presentation.screens.SettingsScreen
import com.example.taskapp.ui.presentation.screens.TaskTrackerScreen
import com.example.taskapp.ui.presentation.settings.SettingsViewModel


private fun NavBackStackEntry.isResumed(): Boolean =
    lifecycle.currentState == Lifecycle.State.RESUMED
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    val viewModel: TaskViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val taskFilter by viewModel.taskFilter.collectAsStateWithLifecycle()
    val priorityFilter by viewModel.priorityFilter.collectAsStateWithLifecycle()
    val operationError by viewModel.operationError.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val showBottomBar = BottomTab.entries.any { tab ->
        currentDestination?.hierarchy?.any { it.route == tab.route } == true
    }

    LaunchedEffect(operationError) {
        operationError?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearOperationError()
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut()
            ) {
                NavigationBar {
                    BottomTab.entries.forEach { tab ->
                        val selected =
                            currentDestination?.hierarchy?.any { it.route == tab.route } == true

                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = null) },
                            label = { Text(stringResource(tab.labelRes)) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.TASKS,

            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        ) {
            composable(Routes.ACCOUNT) {
                AccountScreen()
            }

            composable(Routes.CALENDAR) {
                CalendarScreen()
            }

            composable(Routes.TASKS) { entry ->
                TaskListScreen(
                    uiState = uiState,
                    searchQuery = searchQuery,
                    onSearchQueryChange = viewModel::updateSearchQuery,
                    taskFilter = taskFilter,
                    onTaskFilterChange = viewModel::updateTaskFilter,
                    priorityFilter = priorityFilter,
                    onPriorityFilterChange = viewModel::updatePriorityFilter,
                    onTaskClick = { taskId ->
                        if (entry.isResumed()) {
                            navController.navigate(Routes.edit(taskId)) { launchSingleTop = true }
                        }
                    },
                    onTaskChecked = viewModel::checkTask,
                    onTaskDeleted = viewModel::deleteTask,
                    onAddTask = {
                        if (entry.isResumed()) {
                            viewModel.updateSearchQuery("")
                            navController.navigate(Routes.ADD) { launchSingleTop = true }
                        }
                    }
                )
            }

            composable(Routes.TRACKER) {
                TaskTrackerScreen()
            }

            composable(Routes.SETTINGS) {
                val settingsViewModel: SettingsViewModel = hiltViewModel()
                val settings by settingsViewModel.settings.collectAsStateWithLifecycle()

                SettingsScreen(
                    settings = settings,
                    onThemeChange = settingsViewModel::updateTheme,
                    onTaskLayoutChange = settingsViewModel::updateTaskLayout,
                    onGridColumnsChange = settingsViewModel::updateGridColumns,
                    onCardSizeChange = settingsViewModel::updateCardSize,
                    onPriorityDisplayChange = settingsViewModel::updatePriorityDisplay,
                    onShowDescriptionChange = settingsViewModel::updateShowDescription,
                    onResetSettings = settingsViewModel::resetSettings
                )
            }

            composable(Routes.EDIT) { entry ->
                EditTaskScreen(onBack = { if (entry.isResumed()) navController.popBackStack() })
            }

            composable(Routes.ADD) { entry ->
                AddTaskScreen(onTaskBack = { if (entry.isResumed()) navController.popBackStack() })
            }
        }
    }
}
