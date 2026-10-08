package com.example.taskapp.presentation.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskapp.data.repository.TaskRepository
import com.example.taskapp.domain.model.Task
import com.example.taskapp.presentation.form.TaskFormState
import com.example.taskapp.presentation.task.event.AddTaskEvent
import com.example.taskapp.presentation.task.state.AddTaskUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


@HiltViewModel
class AddTaskViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(AddTaskUiState())

    val uiState: StateFlow<AddTaskUiState> =
        _uiState.asStateFlow()


    private val _events =
        MutableSharedFlow<AddTaskEvent>()

    val event: SharedFlow<AddTaskEvent> =
        _events.asSharedFlow()


    fun onTitleChange(title: String) {
        _uiState.update {
            it.copy(
                form = it.form.copy(
                    title = title
                )
            )
        }
    }


    fun onDescriptionChange(description: String) {
        _uiState.update {
            it.copy(
                form = it.form.copy(
                    description = description
                )
            )
        }
    }


    fun save() {
        if (_uiState.value.isSaving) return

        val form = _uiState.value.form

        if (form.title.isBlank()) return

        _uiState.update {
            it.copy(isSaving = true)
        }

        viewModelScope.launch {
            try {
                repository.addTask(
                    Task(
                        id = 0,
                        title = form.title.trim(),
                        description = form.description.trim()
                    )
                )

                _events.emit(AddTaskEvent.TaskSaved)

            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false) }
                _events.emit(
                    AddTaskEvent.Error(
                        message = e.message ?: "Failed to save task"
                    )
                )
            }
            finally {
                _uiState.update {
                    it.copy(isSaving = false)
                }
            }
        }
    }
}