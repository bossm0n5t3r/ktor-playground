package me.bossm0n5t3r.db

import me.bossm0n5t3r.model.Priority
import me.bossm0n5t3r.model.Task
import org.jetbrains.exposed.v1.core.Transaction
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction

object TaskTable : IntIdTable("task") {
    val name = varchar("name", 50)
    val description = varchar("description", 50)
    val priority = varchar("priority", 50)
}

class TaskDAO(
    id: EntityID<Int>,
) : IntEntity(id) {
    companion object : IntEntityClass<TaskDAO>(TaskTable)

    var name by TaskTable.name
    var description by TaskTable.description
    var priority by TaskTable.priority
}

suspend fun <T> suspendTransaction(block: Transaction.() -> T): T = suspendTransaction(statement = block)

fun daoToModel(dao: TaskDAO) =
    Task(
        dao.name,
        dao.description,
        Priority.valueOf(dao.priority),
    )
