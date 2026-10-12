package com.example.taskapp.data.mapper

import com.example.taskapp.domain.model.Task
import com.example.taskapp.data.local.TaskEntity
import com.example.taskapp.domain.model.TaskPriority

fun TaskEntity.toTask(): Task {
    return Task(
        id = id,
        title = title,
        isCompleted = isCompleted,
        description = description,
        priority = TaskPriority.fromValue(priority)
    )
}

fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        title = title,
        isCompleted = isCompleted,
        description = description,
        priority = priority.value
    )
}