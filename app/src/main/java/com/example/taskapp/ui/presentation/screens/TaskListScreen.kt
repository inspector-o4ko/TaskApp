package com.example.taskapp.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.example.taskapp.presentation.components.TaskList
import com.example.taskapp.domain.model.Task
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.taskapp.domain.model.TaskPriority
import com.example.taskapp.presentation.task.TaskUiState
import com.example.taskapp.ui.presentation.components.DeleteTaskDialog
import com.example.taskapp.ui.presentation.components.EmptyTasksContent
import com.example.taskapp.ui.presentation.task.TaskFilter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    uiState: TaskUiState,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    taskFilter: TaskFilter,
    onTaskFilterChange: (TaskFilter) -> Unit,
    priorityFilter: TaskPriority?,
    onPriorityFilterChange: (TaskPriority?) -> Unit,
    onTaskClick: (Long) -> Unit,
    onTaskChecked: (Task, Boolean) -> Unit,
    onTaskDeleted: (Task) -> Unit,
    onAddTask: () -> Unit
) {
    var taskToDelete by remember { mutableStateOf<Task?>(null) }

    var filtersExpanded by rememberSaveable { mutableStateOf(false) }

    val activeFiltersCount =
        (if (taskFilter != TaskFilter.ALL) 1 else 0) +
        (if (priorityFilter != null) 1 else 0) +
        (if (searchQuery.isNotBlank()) 1 else 0)

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

            //Фильтры
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = { filtersExpanded = !filtersExpanded }) {
                    Text(
                        if (activeFiltersCount > 0) "Filters ($activeFiltersCount)" else "Filters"
                    )
                    Icon(
                        imageVector = if (filtersExpanded) Icons.Default.KeyboardArrowUp
                        else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (filtersExpanded) "Collapse filters" else "Expand filters"
                    )
                }

                if (activeFiltersCount > 0) {
                    TextButton(
                        onClick = {
                            onTaskFilterChange(TaskFilter.ALL)
                            onPriorityFilterChange(null)
                        }
                    ) {
                        Text("Reset")
                    }
                }
            }

            //Фильтры
            AnimatedVisibility(
                visible = filtersExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = taskFilter == TaskFilter.ALL,
                            onClick = { onTaskFilterChange(TaskFilter.ALL) },
                            label = { Text("All") }
                        )
                        FilterChip(
                            selected = taskFilter == TaskFilter.ACTIVE,
                            onClick = { onTaskFilterChange(TaskFilter.ACTIVE) },
                            label = { Text("Active") }
                        )
                        FilterChip(
                            selected = taskFilter == TaskFilter.COMPLETED,
                            onClick = { onTaskFilterChange(TaskFilter.COMPLETED) },
                            label = { Text("Completed") }
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = priorityFilter == null,
                            onClick = { onPriorityFilterChange(null) },
                            label = { Text("Any") }
                        )
                        TaskPriority.entries.forEach { priority ->
                            FilterChip(
                                selected = priorityFilter == priority,
                                onClick = {
                                    onPriorityFilterChange(
                                        if (priorityFilter == priority) null else priority
                                    )
                                },
                                label = { Text(stringResource(priority.labelRes)) }
                            )
                        }
                    }
                }
            }

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

                            searchQuery.isNotBlank() ||
                                    taskFilter != TaskFilter.ALL ||
                                    priorityFilter != null -> Text(
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