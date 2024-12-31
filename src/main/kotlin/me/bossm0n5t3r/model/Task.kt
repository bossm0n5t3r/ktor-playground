package me.bossm0n5t3r.model

import kotlinx.serialization.Serializable

enum class Priority {
    Low,
    Medium,
    High,
    Vital,
}

@Serializable
data class Task(
    val name: String,
    val description: String,
    val priority: Priority,
)
