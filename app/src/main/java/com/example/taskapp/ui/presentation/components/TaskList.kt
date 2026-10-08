package com.example.taskapp.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.taskapp.domain.model.Task

@Composable
fun TaskList(modifier: Modifier = Modifier,
             tasks: List<Task>,
             onTaskChecked: (Task, Boolean) -> Unit,
             onTaskDeleted: (Task) -> Unit,
             onTaskClick: (Long) -> Unit
){
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(tasks, key = { it.id }) { task ->
            TaskItem(
                modifier = Modifier.animateItem(),
                task = task,
                onCheckedChange = { onTaskChecked(task, it) },
                onTaskDeleted = { onTaskDeleted(task) },
                onTaskClick = { onTaskClick(task.id) }
            )
        }
    }
}
