package me.bossm0n5t3r.model

object TaskRepository {
    private val DEFAULT_TASKS =
        listOf(
            Task("cleaning", "Clean the house", Priority.Low),
            Task("gardening", "Mow the lawn", Priority.Medium),
            Task("shopping", "Buy the groceries", Priority.High),
            Task("painting", "Paint the fence", Priority.Medium),
        )

    private val tasks = DEFAULT_TASKS.toMutableList()

    fun allTasks(): List<Task> = tasks

    fun tasksByPriority(priority: Priority) = tasks.filter { it.priority == priority }

    fun taskByName(name: String) =
        tasks.find {
            it.name.equals(name, ignoreCase = true)
        }

    fun addTask(task: Task) {
        if (taskByName(task.name) != null) {
            throw IllegalStateException("Cannot duplicate task names!")
        }
        tasks.add(task)
    }

    fun removeTask(name: String): Boolean = tasks.removeIf { it.name == name }

    fun resetAllTasks() {
        tasks.clear()
        tasks.addAll(DEFAULT_TASKS)
    }
}
