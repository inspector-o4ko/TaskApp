package com.example.taskapp.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.taskapp.R
import com.example.taskapp.domain.model.Task
import com.example.taskapp.domain.model.TaskLayout
import com.example.taskapp.domain.model.TaskPriority

@Composable
fun TaskItem(
    modifier: Modifier = Modifier,
    task: Task,
    layout: TaskLayout = TaskLayout.LIST,
    onCheckedChange: (Boolean) -> Unit,
    onTaskDeleted: () -> Unit,
    onTaskClick: () -> Unit
) {
    var showDescription by rememberSaveable { mutableStateOf(false) }
    val hasDescription = task.description.isNotBlank()

    val alpha by animateFloatAsState(
        targetValue = if (task.isCompleted) 0.5f else 1f,
        label = "taskAlpha"
    )

    val priorityColor = when (task.priority) {
        TaskPriority.HIGH -> Color.Red
        TaskPriority.MEDIUM -> Color.Yellow
        TaskPriority.LOW -> Color.Green
    }

    val isGrid = layout == TaskLayout.GRID

    ElevatedCard(
        onClick = onTaskClick,
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer { this.alpha = alpha },
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            if (!isGrid) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = task.isCompleted,
                        onCheckedChange = onCheckedChange
                    )

                    Text(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp),
                        text = task.title,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        textDecoration = if (task.isCompleted) {
                            TextDecoration.LineThrough
                        } else {
                            TextDecoration.None
                        }
                    )
                    if (hasDescription) {
                        DescriptionToggleButton(showDescription) { showDescription = !showDescription }
                    }

                    IconButton(onClick = onTaskDeleted) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete task",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
                AnimatedVisibility(visible = showDescription && hasDescription) {
                    DescriptionText(
                        text = task.description,
                        modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 12.dp)
                    )
                }
            } else {
                Column(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = task.isCompleted,
                            onCheckedChange = onCheckedChange,
                            modifier = Modifier.size(48.dp)
                        )

                        IconButton(
                            onClick = onTaskDeleted,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete task",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 4.dp),
                            text = task.title,
                            style = MaterialTheme.typography.bodyLarge,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                        )

                        if (hasDescription) {
                            DescriptionToggleButton(showDescription) { showDescription = !showDescription }
                        }
                    }
                    AnimatedVisibility(visible = showDescription && hasDescription) {
                        DescriptionText(
                            text = task.description,
                            modifier = Modifier.padding(start = 4.dp, end = 4.dp, bottom = 8.dp)
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(priorityColor)
            )
        }
    }
}

@Composable
private fun DescriptionToggleButton(
    expanded: Boolean,
    onClick: () -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "arrowRotation"
    )
    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = stringResource(
                if (expanded) R.string.collapse_description
                else R.string.expand_description
            ),
            modifier = Modifier.rotate(rotation)
        )
    }
}

@Composable
private fun DescriptionText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
    )
}