package com.example.taskapp.domain.model

data class Task(
    val id: Long,
    val title: String,
    val isCompleted: Boolean = false,
    val description: String = "",
    val priority: TaskPriority = TaskPriority.LOW
)