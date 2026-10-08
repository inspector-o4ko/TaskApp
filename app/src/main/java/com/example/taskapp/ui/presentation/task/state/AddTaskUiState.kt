package com.example.taskapp.presentation.task.state

import com.example.taskapp.presentation.form.TaskFormState



data class AddTaskUiState(
    val form: TaskFormState = TaskFormState(),
    val isSaving: Boolean = false
)