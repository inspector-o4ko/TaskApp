package com.example.taskapp.domain.model

import androidx.annotation.StringRes
import com.example.taskapp.R

enum class TaskPriority(val value: Int, @StringRes val labelRes: Int) {
    LOW(0, R.string.priority_low),
    MEDIUM(1, R.string.priority_medium),
    HIGH(2, R.string.priority_high);

    companion object {
        fun fromValue(value: Int): TaskPriority =
            entries.firstOrNull { it.value == value } ?: LOW
    }
}