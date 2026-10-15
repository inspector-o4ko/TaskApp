package com.example.taskapp.presentation.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.taskapp.domain.model.Task
import com.example.taskapp.domain.model.TaskLayout

@Composable
fun TaskList(
    modifier: Modifier = Modifier,
    tasks: List<Task>,
    layout: TaskLayout = TaskLayout.LIST,
    onTaskChecked: (Task, Boolean) -> Unit,
    onTaskDeleted: (Task) -> Unit,
    onTaskClick: (Long) -> Unit
) {
    val contentPadding = PaddingValues(
        start = 16.dp,
        end = 16.dp,
        top = 8.dp,
        bottom = 88.dp
    )

    val listState = rememberLazyListState()
    val gridState = rememberLazyStaggeredGridState()

    SideEffect {
        when (layout) {
            TaskLayout.LIST -> listState.requestScrollToItem(
                index = listState.firstVisibleItemIndex,
                scrollOffset = listState.firstVisibleItemScrollOffset
            )
            TaskLayout.GRID -> gridState.requestScrollToItem(
                index = gridState.firstVisibleItemIndex,
                scrollOffset = gridState.firstVisibleItemScrollOffset
            )
        }
    }

    var teleportTaskId by remember { mutableStateOf<Long?>(null) }

    val placementSpec = spring(
        stiffness = Spring.StiffnessMediumLow,
        visibilityThreshold = IntOffset.VisibilityThreshold
    )

    val onChecked: (Task, Boolean) -> Unit = { task, checked ->
        val firstVisibleIndex = when (layout) {
            TaskLayout.LIST -> listState.firstVisibleItemIndex
            TaskLayout.GRID -> gridState.firstVisibleItemIndex
        }

        val newIndex = tasks
            .map { if (it.id == task.id) it.copy(isCompleted = checked) else it }
            .sortedWith(
                compareBy<Task> { it.isCompleted }
                    .thenByDescending { it.priority.value }
                    .thenByDescending { it.id }
            )
            .indexOfFirst { it.id == task.id }

        teleportTaskId = if (newIndex < firstVisibleIndex) task.id else null

        onTaskChecked(task, checked)
    }

    when (layout) {
        TaskLayout.LIST -> {
            LazyColumn(
                modifier = modifier,
                state = listState,
                contentPadding = contentPadding,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = tasks,
                    key = { it.id }
                ) { task ->
                    TaskItem(
                        modifier = Modifier.animateItem(
                            placementSpec = if (task.id == teleportTaskId) null else placementSpec
                        ),
                        task = task,
                        layout = layout,
                        onCheckedChange = { onChecked(task, it) },
                        onTaskDeleted = { onTaskDeleted(task) },
                        onTaskClick = { onTaskClick(task.id) }
                    )
                }
            }
        }

        TaskLayout.GRID -> {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                state = gridState,
                modifier = modifier,
                contentPadding = contentPadding,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalItemSpacing = 8.dp
            ) {
                items(
                    items = tasks,
                    key = { it.id }
                ) { task ->
                    TaskItem(
                        modifier = Modifier.animateItem(
                            placementSpec = if (task.id == teleportTaskId) null else placementSpec
                        ),
                        task = task,
                        layout = layout,
                        onCheckedChange = { onChecked(task, it) },
                        onTaskDeleted = { onTaskDeleted(task) },
                        onTaskClick = { onTaskClick(task.id) }
                    )
                }
            }
        }
    }
}