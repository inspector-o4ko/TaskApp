package com.example.taskapp.presentation.task

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskapp.data.repository.TaskRepository
import com.example.taskapp.domain.model.Task
import com.example.taskapp.presentation.form.TaskFormState
import com.example.taskapp.ui.presentation.task.event.EditTaskEvent
import com.example.taskapp.ui.presentation.task.state.EditTaskUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditTaskViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: TaskRepository
) : ViewModel() {

    private val taskId: Long =
        checkNotNull(
            savedStateHandle.get<String>("taskId")
        ).toLong()


    private val _uiState = MutableStateFlow(EditTaskUiState())

    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<EditTaskEvent>()

    val events = _events.asSharedFlow()


    init {
        loadTask()
    }

    private fun loadTask() {
        viewModelScope.launch {
            val task = repository.getTask(taskId).first()

            if (task != null) {
                _uiState.update {
                    it.copy(
                        form = TaskFormState(
                            title = task.title,
                            description = task.description
                        ),
                        isLoading = false
                    )
                }
            } else {
                _events.emit(EditTaskEvent.Error("Task not found"))
            }
        }
    }

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

        val state = _uiState.value

        if (state.form.title.isBlank()) return

        _uiState.update {
            it.copy(isSaving = true)
        }

        viewModelScope.launch {
            try {
                repository.updateTask(
                    Task(
                        id = taskId,
                        title = state.form.title.trim(),
                        description = state.form.description.trim()
                    )
                )

                _events.emit(
                    EditTaskEvent.TaskUpdated
                )
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false) }
                _events.emit(
                    EditTaskEvent.Error(
                        e.message ?: "Failed to update task"
                    )
                )
            }finally {
                _uiState.update {
                    it.copy(isSaving = false)
                }
            }
        }
    }
}