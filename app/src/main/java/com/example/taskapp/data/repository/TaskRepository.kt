package com.example.taskapp.data.repository

import com.example.taskapp.domain.model.Task
import com.example.taskapp.data.local.TaskDao
import com.example.taskapp.data.mapper.toEntity
import com.example.taskapp.data.mapper.toTask
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TaskRepository @Inject constructor(
    private val dao: TaskDao
) {
    fun getTasks(): Flow<List<Task>> {
        return dao.getAllTasks()
            .map { entities ->
                entities.map { entity ->
                    entity.toTask()
                }
            }
    }
    fun getTask(taskId: Int): Flow<Task?>{
        return dao.getTaskById(taskId)
            .map { entity ->
                entity?.toTask()
            }
    }

    suspend fun addTask(task: Task) {
        dao.insertTask(
            task.toEntity()
        )
    }

    suspend fun updateTask(task: Task) {
        dao.updateTask(
            task.toEntity()
        )
    }

    suspend fun deleteTask(task: Task) {
        dao.deleteTask(
            task.toEntity()
        )
    }
}