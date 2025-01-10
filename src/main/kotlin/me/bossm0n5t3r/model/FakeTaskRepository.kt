package me.bossm0n5t3r.model

class FakeTaskRepository : TaskRepository {
    companion object {
        private val DEFAULT_TASKS =
            listOf(
                Task("cleaning", "Clean the house", Priority.Low),
                Task("gardening", "Mow the lawn", Priority.Medium),
                Task("shopping", "Buy the groceries", Priority.High),
                Task("painting", "Paint the fence", Priority.Medium),
            )
    }

    private val tasks = DEFAULT_TASKS.toMutableList()

    override fun allTasks(): List<Task> = tasks

    override fun tasksByPriority(priority: Priority) = tasks.filter { it.priority == priority }

    override fun taskByName(name: String) =
        tasks.find {
            it.name.equals(name, ignoreCase = true)
        }

    override fun addTask(task: Task) {
        if (taskByName(task.name) != null) {
            throw IllegalStateException("Cannot duplicate task names!")
        }
        tasks.add(task)
    }

    override fun removeTask(name: String): Boolean = tasks.removeIf { it.name == name }

    fun resetAllTasks() {
        tasks.clear()
        tasks.addAll(DEFAULT_TASKS)
    }
}
