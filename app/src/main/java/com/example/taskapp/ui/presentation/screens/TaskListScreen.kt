package com.example.taskapp.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.taskapp.presentation.components.TaskList
import com.example.taskapp.domain.model.Task
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.example.taskapp.presentation.task.TaskUiState
import com.example.taskapp.ui.presentation.components.DeleteTaskDialog
import com.example.taskapp.ui.presentation.components.EmptyTasksContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    uiState: TaskUiState,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onTaskClick: (Long) -> Unit,
    onTaskChecked: (Task, Boolean) -> Unit,
    onTaskDeleted: (Task) -> Unit,
    onAddTask: () -> Unit
) {
    var taskToDelete by remember { mutableStateOf<Task?>(null) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(title = { Text("Task App") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTask) {
                Icon(Icons.Default.Add, contentDescription = "Add task")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search tasks") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear search")
                        }
                    }
                },
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                when (uiState) {
                    is TaskUiState.Loading -> CircularProgressIndicator()

                    is TaskUiState.Error -> Text(
                        text = uiState.message,
                        color = MaterialTheme.colorScheme.error
                    )

                    is TaskUiState.Success -> {
                        when {
                            uiState.tasks.isNotEmpty() -> TaskList(
                                modifier = Modifier.fillMaxSize(),
                                tasks = uiState.tasks,
                                onTaskChecked = onTaskChecked,
                                onTaskDeleted = { taskToDelete = it },
                                onTaskClick = onTaskClick
                            )

                            searchQuery.isNotBlank() -> Text(
                                text = "Nothing found",
                                style = MaterialTheme.typography.bodyLarge
                            )

                            else -> EmptyTasksContent(
                                onAddTask = onAddTask,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }

    taskToDelete?.let { task ->
        DeleteTaskDialog(
            task = task,
            onConfirm = {
                onTaskDeleted(task)
                taskToDelete = null
            },
            onDismiss = { taskToDelete = null }
        )
    }
}