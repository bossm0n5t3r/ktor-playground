package me.bossm0n5t3r

import io.ktor.server.application.Application
import io.ktor.server.http.content.staticResources
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import me.bossm0n5t3r.model.Priority
import me.bossm0n5t3r.model.Task

fun Application.configureRouting() {
    routing {
        staticResources("static", "static")

        get("/") {
            call.respondText("Hello World!")
        }

        get("/tasks") {
            call.respond(
                listOf(
                    Task("cleaning", "Clean the house", Priority.Low),
                    Task("gardening", "Mow the lawn", Priority.Medium),
                    Task("shopping", "Buy the groceries", Priority.High),
                    Task("painting", "Paint the fence", Priority.Medium),
                ),
            )
        }
    }
}
