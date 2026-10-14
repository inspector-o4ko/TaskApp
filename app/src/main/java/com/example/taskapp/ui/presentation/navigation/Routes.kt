package com.example.taskapp.presentation.navigation

object Routes {
    const val ACCOUNT = "account"
    const val CALENDAR = "calendar"
    const val TASKS = "tasks"
    const val TRACKER = "tracker"
    const val SETTINGS = "settings"
    const val ADD = "add"
    const val EDIT = "edit/{taskId}"
    fun edit(id: Long) = "edit/$id"
}