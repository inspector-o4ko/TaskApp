package com.example.taskapp.domain.model

enum class AppTheme {
    SYSTEM,
    LIGHT,
    DARK
}

enum class TaskLayout {
    LIST,
    GRID
}

enum class PriorityDisplay {
    HIDDEN,
    COLOR_STRIP,
    ICON,
    LABEL
}

data class AppSettings(
    val theme: AppTheme = AppTheme.SYSTEM,
    val taskLayout: TaskLayout = TaskLayout.LIST,
    val priorityDisplay: PriorityDisplay = PriorityDisplay.COLOR_STRIP,
    val showDescription: Boolean = true
)