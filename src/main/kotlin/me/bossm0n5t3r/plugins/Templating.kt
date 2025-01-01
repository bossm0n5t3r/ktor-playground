package me.bossm0n5t3r.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.thymeleaf.Thymeleaf
import io.ktor.server.thymeleaf.ThymeleafContent
import me.bossm0n5t3r.model.Priority
import me.bossm0n5t3r.model.Task
import me.bossm0n5t3r.model.ThymeleafUser
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver

fun Application.configureTemplating() {
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
        get("/html-thymeleaf") {
            call.respond(
                ThymeleafContent(
                    "index",
                    mapOf("user" to ThymeleafUser(1, "user1")),
                ),
            )
        }

        get("/website/tasks") {
            val tasks =
                listOf(
                    Task("cleaning", "Clean the house", Priority.Low),
                    Task("gardening", "Mow the lawn", Priority.Medium),
                    Task("shopping", "Buy the groceries", Priority.High),
                    Task("painting", "Paint the fence", Priority.Medium),
                )
            call.respond(ThymeleafContent("all-tasks", mapOf("tasks" to tasks)))
        }
    }
}
