package com.example.taskapp.ui.presentation.task.state

import com.example.taskapp.presentation.form.TaskFormState


data class EditTaskUiState(
    val form: TaskFormState = TaskFormState(),
    val isLoading: Boolean = true,
    val isSaving: Boolean = false
)