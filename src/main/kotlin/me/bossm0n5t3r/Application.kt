package me.bossm0n5t3r

import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain
import me.bossm0n5t3r.plugins.configureRouting
import me.bossm0n5t3r.plugins.configureSerialization

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    configureRouting()
    configureSerialization()
}
