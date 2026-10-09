package com.example.taskapp.presentation.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskapp.data.repository.TaskRepository
import com.example.taskapp.domain.model.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel(){

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    val uiState: StateFlow<TaskUiState> = combine<List<Task>, String, TaskUiState>(
        repository.getTasks(),
        searchQuery
    ) { tasks, query ->
        val q = query.trim()
        val filteredTasks = if (q.isEmpty()) {
            tasks
        } else {
            tasks.filter { task ->
                task.title.contains(q, ignoreCase = true) ||
                        task.description.contains(q, ignoreCase = true)
            }
        }
        TaskUiState.Success(filteredTasks)
    }   .catch { error ->
            emit(TaskUiState.Error(error.message ?: "Unknown error"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TaskUiState.Loading
        )

    fun deleteTask(task: Task){
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun checkTask(task: Task, isChecked: Boolean){
        viewModelScope.launch {
            repository.updateTask(
                task.copy(
                    isCompleted = isChecked
                )
            )
        }
    }
}