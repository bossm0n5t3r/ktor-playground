package me.bossm0n5t3r.plugins

import io.ktor.server.application.Application
import me.bossm0n5t3r.db.TaskTable
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.deleteAll
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

fun Application.configureDatabases() {
    val database =
        Database.connect(
            url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
            user = "root",
            driver = "org.h2.Driver",
            password = "",
        )

    transaction(database) { initializeDatabase() }
}

private fun initializeDatabase() {
    SchemaUtils.drop(TaskTable)
    SchemaUtils.create(TaskTable)

    TaskTable.deleteAll()
    TaskTable.insert {
        it[name] = "cleaning"
        it[description] = "Clean the house"
        it[priority] = "Low"
    }
    TaskTable.insert {
        it[name] = "gardening"
        it[description] = "Mow the lawn"
        it[priority] = "Medium"
    }
    TaskTable.insert {
        it[name] = "shopping"
        it[description] = "Buy the groceries"
        it[priority] = "High"
    }
    TaskTable.insert {
        it[name] = "painting"
        it[description] = "Paint the fence"
        it[priority] = "Medium"
    }
}
