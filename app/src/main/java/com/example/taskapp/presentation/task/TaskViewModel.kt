package com.example.taskapp.presentation.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskapp.data.repository.TaskRepository
import com.example.taskapp.domain.model.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel(){
    val uiState: StateFlow<TaskUiState> = repository.getTasks()
        .map<List<Task>, TaskUiState> { tasks ->
            TaskUiState.Success(tasks)
        }
        .catch { error ->
            emit(
                TaskUiState.Error(
                    error.message ?: "Unknown error"
                )
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TaskUiState.Loading
        )

    fun addTask(title: String){
        viewModelScope.launch {
            repository.addTask(
                Task(
                    id = 0,
                    title = title
                )
            )
        }
    }

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