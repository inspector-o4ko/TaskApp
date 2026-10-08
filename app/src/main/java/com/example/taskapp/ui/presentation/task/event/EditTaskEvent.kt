package com.example.taskapp.ui.presentation.task.event

sealed interface EditTaskEvent {

    data object TaskUpdated : EditTaskEvent

    data class Error(
        val message: String
    ) : EditTaskEvent
}