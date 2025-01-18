package me.bossm0n5t3r.db

import kotlinx.coroutines.Dispatchers
import me.bossm0n5t3r.model.Priority
import me.bossm0n5t3r.model.Task
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

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

suspend fun <T> suspendTransaction(
    database: Database,
    block: Transaction.() -> T,
): T = newSuspendedTransaction(Dispatchers.IO, statement = block, db = database)

fun daoToModel(dao: TaskDAO) =
    Task(
        dao.name,
        dao.description,
        Priority.valueOf(dao.priority),
    )
