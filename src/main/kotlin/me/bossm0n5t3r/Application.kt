package me.bossm0n5t3r

import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain
import me.bossm0n5t3r.model.FakeTaskRepository
import me.bossm0n5t3r.plugins.configureRouting
import me.bossm0n5t3r.plugins.configureSerialization
import me.bossm0n5t3r.plugins.configureSockets
import me.bossm0n5t3r.plugins.configureTemplating

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    val repository = FakeTaskRepository()

    configureRouting(repository)
    configureSerialization()
    configureTemplating(repository)
    configureSockets(repository)
}
