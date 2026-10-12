package com.example.taskapp.presentation.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskapp.data.repository.TaskRepository
import com.example.taskapp.domain.model.Task
import com.example.taskapp.domain.model.TaskPriority
import com.example.taskapp.ui.presentation.task.TaskFilter
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
import kotlin.collections.List

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel(){

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    private val _taskFilter = MutableStateFlow(TaskFilter.ALL)
    val taskFilter = _taskFilter.asStateFlow()
    fun updateTaskFilter(filter: TaskFilter){
        _taskFilter.value = filter
    }

    private val _priorityFilter = MutableStateFlow<TaskPriority?>(null)
    val priorityFilter = _priorityFilter.asStateFlow()
    fun updatePriorityFilter(priority: TaskPriority?) {
        _priorityFilter.value = priority
    }

    val uiState: StateFlow<TaskUiState> =
        combine<List<Task>, String, TaskFilter, TaskPriority?, TaskUiState>(
            repository.getTasks(),
            searchQuery,
            taskFilter,
            priorityFilter
        ) { tasks, query, filter, priority ->

            val q = query.trim()

            val searchedTasks = tasks.filter { task ->
                q.isEmpty() ||
                        task.title.contains(q, ignoreCase = true) ||
                        task.description.contains(q, ignoreCase = true)
            }

            val statusFiltered = when (filter) {
                TaskFilter.ALL -> searchedTasks
                TaskFilter.ACTIVE -> searchedTasks.filter { !it.isCompleted }
                TaskFilter.COMPLETED -> searchedTasks.filter { it.isCompleted }
            }

            val filteredTasks = if (priority == null) {
                statusFiltered
            } else {
                statusFiltered.filter { it.priority == priority }
            }

            TaskUiState.Success(filteredTasks)
        }
            .catch { error ->
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