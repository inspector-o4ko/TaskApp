package com.example.taskapp.presentation.form

import com.example.taskapp.domain.model.TaskPriority

data class TaskFormState(
    val title: String = "",
    val description: String = "",
    val priority: TaskPriority = TaskPriority.LOW
)