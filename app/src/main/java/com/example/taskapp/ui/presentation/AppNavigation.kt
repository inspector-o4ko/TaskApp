package com.example.taskapp.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.taskapp.presentation.screens.AddTaskScreen
import com.example.taskapp.presentation.screens.EditTaskScreen
import com.example.taskapp.presentation.navigation.Routes
import com.example.taskapp.presentation.screens.TaskListScreen
import com.example.taskapp.presentation.task.EditTaskViewModel
import com.example.taskapp.presentation.task.TaskUiState
import com.example.taskapp.presentation.task.TaskViewModel


private fun NavBackStackEntry.isResumed(): Boolean =
    lifecycle.currentState == Lifecycle.State.RESUMED

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    val viewModel: TaskViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = Routes.TASKS
    ) {

        composable(Routes.TASKS) { backStackEntry ->
            TaskListScreen(
                uiState = uiState,
                searchQuery = searchQuery,
                onSearchQueryChange = viewModel::updateSearchQuery,
                onTaskClick = { taskId ->
                    if (backStackEntry.isResumed()) {
                        navController.navigate(Routes.edit(taskId)) {
                            launchSingleTop = true
                        }
                    }
                },
                onTaskChecked = { task, isChecked ->
                    viewModel.checkTask(task, isChecked)
                },
                onTaskDeleted = { task ->
                    viewModel.deleteTask(task)
                },
                onAddTask = {
                    if (backStackEntry.isResumed()) {
                        viewModel.updateSearchQuery("")
                        navController.navigate(Routes.ADD) { launchSingleTop = true }
                    }
                }
            )
        }

        composable(Routes.EDIT) { backStackEntry ->
            EditTaskScreen(
                onBack = {
                    if (backStackEntry.isResumed()) {
                        navController.popBackStack()
                    }
                }
            )
        }

        composable(Routes.ADD) { backStackEntry ->
            AddTaskScreen(
                onTaskBack = {
                    if (backStackEntry.isResumed()) {
                        navController.popBackStack()
                    }
                }
            )
        }
    }
}