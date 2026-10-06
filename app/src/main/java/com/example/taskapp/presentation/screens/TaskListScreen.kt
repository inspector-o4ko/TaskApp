package com.example.taskapp.presentation.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.taskapp.presentation.components.TaskList
import com.example.taskapp.domain.model.Task

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    tasks: List<Task>,
    onTaskClick: (Long) -> Unit,
    onTaskChecked: (Task, Boolean) -> Unit,
    onTaskDeleted: (Task) -> Unit,
    onAddTask: () -> Unit
) {

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Task App") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTask) {
                Icon(Icons.Default.Add, contentDescription = "Add task")
            }
        }
    ) { innerPadding ->
        TaskList(
            modifier = Modifier.padding(innerPadding),
            tasks = tasks,
            onTaskChecked = onTaskChecked,
            onTaskDeleted = onTaskDeleted,
            onTaskClick = onTaskClick
        )
    }
}
