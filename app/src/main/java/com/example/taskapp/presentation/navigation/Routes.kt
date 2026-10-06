package com.example.taskapp.presentation.navigation

object Routes {
    const val TASKS = "tasks"
    const val ADD = "add"
    const val EDIT = "edit/{taskId}"
    fun edit(id: Long) = "edit/$id"
}