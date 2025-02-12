package me.bossm0n5t3r.model

interface TaskRepository {
    suspend fun allTasks(): List<Task>

    suspend fun tasksByPriority(priority: Priority): List<Task>

    suspend fun taskByName(name: String): Task?

    suspend fun addTask(task: Task)

    suspend fun removeTask(name: String): Boolean
}
