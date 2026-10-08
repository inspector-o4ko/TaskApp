package com.example.taskapp.presentation.task.event

sealed interface AddTaskEvent {

    data object TaskSaved : AddTaskEvent

    data class Error(
        val message: String
    ) : AddTaskEvent

}