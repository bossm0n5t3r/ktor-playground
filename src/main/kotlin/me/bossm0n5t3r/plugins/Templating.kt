package me.bossm0n5t3r.plugins

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.thymeleaf.Thymeleaf
import io.ktor.server.thymeleaf.ThymeleafContent
import me.bossm0n5t3r.model.Priority
import me.bossm0n5t3r.model.Task
import me.bossm0n5t3r.model.TaskRepository
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver

fun Application.configureTemplating(repository: TaskRepository) {
    install(Thymeleaf) {
        setTemplateResolver(
            ClassLoaderTemplateResolver().apply {
                prefix = "templates/thymeleaf/"
                suffix = ".html"
                characterEncoding = "utf-8"
            },
        )
    }

    routing {
        route("/website/tasks") {
            get {
                val tasks = repository.allTasks()
                call.respond(
                    ThymeleafContent("all-tasks", mapOf("tasks" to tasks)),
                )
            }

            get("/byName") {
                val name = call.request.queryParameters["name"]
                if (name == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                val task = repository.taskByName(name)
                if (task == null) {
                    call.respond(HttpStatusCode.NotFound)
                    return@get
                }
                call.respond(
                    ThymeleafContent("single-task", mapOf("task" to task)),
                )
            }

            get("/byPriority") {
                val priorityAsText = call.request.queryParameters["priority"]
                if (priorityAsText == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                try {
                    val priority = Priority.valueOf(priorityAsText)
                    val tasks = repository.tasksByPriority(priority)

                    if (tasks.isEmpty()) {
                        call.respond(HttpStatusCode.NotFound)
                        return@get
                    }
                    val data =
                        mapOf(
                            "priority" to priority,
                            "tasks" to tasks,
                        )
                    call.respond(ThymeleafContent("tasks-by-priority", data))
                } catch (ex: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }

            post {
                val formContent = call.receiveParameters()
                val params =
                    Triple(
                        formContent["name"] ?: "",
                        formContent["description"] ?: "",
                        formContent["priority"] ?: "",
                    )
                if (params.toList().any { it.isEmpty() }) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }
                try {
                    val priority = Priority.valueOf(params.third)
                    repository.addTask(
                        Task(
                            params.first,
                            params.second,
                            priority,
                        ),
                    )
                    val tasks = repository.allTasks()
                    call.respond(
                        ThymeleafContent("all-tasks", mapOf("tasks" to tasks)),
                    )
                } catch (ex: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest)
                } catch (ex: IllegalStateException) {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
        }
    }
}
